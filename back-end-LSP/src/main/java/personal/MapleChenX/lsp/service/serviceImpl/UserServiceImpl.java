package personal.MapleChenX.lsp.service.serviceImpl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personal.MapleChenX.lsp.common.Const;
import personal.MapleChenX.lsp.common.enums.CodeEnum;
import personal.MapleChenX.lsp.common.model.dto.UserBasicInfoDTO;
import personal.MapleChenX.lsp.common.model.mid.EmailAndCode;
import personal.MapleChenX.lsp.common.model.req.AddBlacklistReq;
import personal.MapleChenX.lsp.common.model.req.LoginReq;
import personal.MapleChenX.lsp.common.model.req.PasswordModifyReq;
import personal.MapleChenX.lsp.common.model.req.RegisterReq;
import personal.MapleChenX.lsp.common.model.resp.RegisterResp;
import personal.MapleChenX.lsp.common.model.ret.ResponseTemplate;
import personal.MapleChenX.lsp.mapper.UserMapper;
import personal.MapleChenX.lsp.service.UserService;
import personal.MapleChenX.lsp.utils.GracefulCodeGenerator;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static personal.MapleChenX.lsp.common.enums.UserEnum.*;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserBasicInfoDTO> implements UserService, UserDetailsService {
    @Value("${const.default.user.username}")
    String defaultUsername;

    @Value("${const.default.user.role}")
    int defaultRole;

    @Value("${const.default.user.avatar}")
    String defaultAvatar;

    @Value("${const.default.codeVerifyTime}")
    private int codeVerifyTime;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RabbitTemplate rabbitTemplate;

    // TODO 2 实现接口，重写方法，载入真实密码
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        UserBasicInfoDTO one = this.query()
                .eq("account", account).or()
                .eq("email", account)
                .one();
        if(one == null)
            throw new UsernameNotFoundException("不存在此用户");
        return User
                .withUsername(account)
                .password(one.getPassword())
                .roles(String.valueOf(one.getRole()))
                .build();
    }

    public boolean registerAuthorize(String email){
        // 校验该邮箱是否已经注册
        QueryWrapper<UserBasicInfoDTO> query = new QueryWrapper<UserBasicInfoDTO>();
        query.eq("email", email);
        UserBasicInfoDTO userBasicInfoDTO = this.getOne(query);
        return userBasicInfoDTO != null;
    }

    @Override
    public ResponseTemplate<?> sendCode(String email, int type) {
        if(type == CodeEnum.REGISTER.getCode()){
            if(registerAuthorize(email)){
                return ResponseTemplate.failure(EMAIL_IN_USE.getCode(), EMAIL_IN_USE.getMsg());
            }
        }

        EmailAndCode emailAndCode = new EmailAndCode();
        emailAndCode.setEmail(email);
        // 生成更好记的验证码
        int code = GracefulCodeGenerator.codeGenerate();
        emailAndCode.setCode(code);
        // 转换emailAndCode为json
        String jsonString = JSON.toJSONString(emailAndCode);

        if(type == CodeEnum.REGISTER.getCode()){
            rabbitTemplate.convertAndSend(Const.CODE_SEND_EXCHANGER, Const.ROUTING_KEY_OF_REGISTER, jsonString);
            // 塞进redis,3分钟内有效
            stringRedisTemplate.opsForValue()
                    .set(Const.REGISTER_VERIFY_CODE + email, String.valueOf(code), codeVerifyTime, TimeUnit.MINUTES);
        }else if(type == CodeEnum.PASSWORD_MODIFY.getCode()){
            rabbitTemplate.convertAndSend(Const.CODE_SEND_EXCHANGER, Const.ROUTING_KEY_OF_PASSWORD_MODIFY, jsonString);
            stringRedisTemplate.opsForValue()
                    .set(Const.PASSWORD_MODIFY_VERIFY_CODE + email, String.valueOf(code), codeVerifyTime, TimeUnit.MINUTES);
        }else{
            rabbitTemplate.convertAndSend(Const.CODE_SEND_EXCHANGER, Const.DEFAULT_ROUTING_KEY, jsonString);
            stringRedisTemplate.opsForValue()
                    .set(Const.DEFAULT_EMAIL_CODE + email, String.valueOf(code), codeVerifyTime, TimeUnit.MINUTES);
        }

        return ResponseTemplate.success("发送邮件成功");
    }

    @Override
    @Transactional
    public synchronized ResponseTemplate<?> register(RegisterReq req, String code) {
        // 校验验证码
        String key = Const.REGISTER_VERIFY_CODE + req.getEmail();
        String realCode = stringRedisTemplate.opsForValue().get(key);

        if(realCode == null){
            return ResponseTemplate.failure(VERIFY_CODE_EXPIRED.getCode(), VERIFY_CODE_EXPIRED.getMsg());
        }

        if(!realCode.equals(code)){
            return ResponseTemplate.failure(VERIFY_CODE_NOT_EXIST.getCode(),VERIFY_CODE_NOT_EXIST.getMsg());
        }
        // 验证码通过，删除验证码
        stringRedisTemplate.delete(key);

        // 校验邮箱是否已注册
        QueryWrapper<UserBasicInfoDTO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", req.getEmail());
        UserBasicInfoDTO userBasicInfoDTO = this.getOne(queryWrapper);
        if(userBasicInfoDTO != null){
            return ResponseTemplate.failure(EMAIL_IN_USE.getCode(),EMAIL_IN_USE.getMsg());
        }
        // 校验用户账号是否已注册
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", req.getAccount());
        userBasicInfoDTO = this.getOne(queryWrapper);
        if(userBasicInfoDTO != null){
            return ResponseTemplate.failure(USERNAME_IN_USE.getCode(),USERNAME_IN_USE.getMsg());
        }

        UserBasicInfoDTO userBasicInfoDTOInsert = new UserBasicInfoDTO();
        BeanUtils.copyProperties(req, userBasicInfoDTOInsert);
        UUID uuid = UUID.randomUUID();
        String uuidNo = uuid.toString().replace("-", "");
        userBasicInfoDTOInsert.setId(uuidNo);
        userBasicInfoDTOInsert.setUsername(defaultUsername);
        userBasicInfoDTOInsert.setRole(defaultRole);
        userBasicInfoDTOInsert.setAvatar(defaultAvatar);
        // 注册时间在server生成
        long timestamp = System.currentTimeMillis();
        userBasicInfoDTOInsert.setRegisterTime(new Timestamp(timestamp));
        // service用save
        this.save(userBasicInfoDTOInsert);

        // 返回用户信息
        UserBasicInfoDTO dto = getUserInfoByAccount(req.getAccount());

        RegisterResp registerResp = new RegisterResp();
        BeanUtils.copyProperties(dto, registerResp);

        return ResponseTemplate.success(registerResp);
    }

    public UserBasicInfoDTO getUserInfoByAccount(String account) {
        QueryWrapper<UserBasicInfoDTO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        return this.getOne(queryWrapper);
    }

    // [Deprecated]
    @Override
    public ResponseTemplate<?> login(LoginReq req) {

        // 校验账号是否存在
        UserBasicInfoDTO dto = this.query()
                .eq("account", req.getAccount()).or()
                .eq("email", req.getEmail())
                .one();
        if (dto == null) {
            return ResponseTemplate.failure(ACCOUNT_NOT_EXIST.getCode(), ACCOUNT_NOT_EXIST.getMsg());
        }
        // 校验密码是否正确
        if (!dto.getPassword().equals(req.getPassword())) {
            return ResponseTemplate.failure(PASSWORD_NOT_RIGHT.getCode(), PASSWORD_NOT_RIGHT.getMsg());
        }
        RegisterResp registerResp = new RegisterResp();
        BeanUtils.copyProperties(dto, registerResp);
        return ResponseTemplate.success(registerResp);

    }

    // 用户封禁
    @Override
    public ResponseTemplate<?> addBlacklist(AddBlacklistReq req) {
        doAuthorize(String.valueOf(req.getAdminId())); //管理员校验
        checkUserPeriod(String.valueOf(req.getUserId())); //封禁验证
        doUserPeriod(req); //封禁
        return ResponseTemplate.success("封禁成功");
    }

    @Override
    public ResponseTemplate<?> passwordModify(PasswordModifyReq req, String code) {
        // 校验验证码
        String key = Const.PASSWORD_MODIFY_VERIFY_CODE + req.getEmail();
        String realCode = stringRedisTemplate.opsForValue().get(key);
        if(realCode == null)    {
            return ResponseTemplate.failure(VERIFY_CODE_EXPIRED.getCode(), VERIFY_CODE_EXPIRED.getMsg());
        }
        if (!realCode.isEmpty() && realCode.equals(code)) {
            // 校验账号是否存在
            UserBasicInfoDTO dto = this.query()
                    .eq("email", req.getEmail())
                    .one();
            if (dto == null) {
                return ResponseTemplate.failure(ACCOUNT_NOT_EXIST.getCode(), ACCOUNT_NOT_EXIST.getMsg());
            }
            if(!Objects.equals(dto.getPassword(), req.getOldPassword()))
                return ResponseTemplate.failure(PASSWORD_NOT_RIGHT.getCode(), PASSWORD_NOT_RIGHT.getMsg());
            // 修改密码
            dto.setPassword(req.getNewPassword());
            this.updateById(dto);
            // 通过，删除验证码
            stringRedisTemplate.delete(key);
            return ResponseTemplate.success("修改密码成功");
        } else {
            return ResponseTemplate.failure(VERIFY_CODE_NOT_EXIST.getCode(), VERIFY_CODE_NOT_EXIST.getMsg());
        }
    }

    @Override
    public void updateAvatar(String avatar, String uid) {
        UserBasicInfoDTO dto = this.query().eq("id", uid).one();
        dto.setAvatar(avatar);
        this.updateById(dto);
    }

    private void doUserPeriod(String userId){
        stringRedisTemplate.opsForValue().set(Const.USER_PERIOD + userId, "永久封禁");
    }

    private void doUserPeriod(String userId, int days){
        stringRedisTemplate.opsForValue().set(Const.USER_PERIOD + userId, "有限封禁", days, TimeUnit.DAYS);
    }

    private void doUserPeriod(AddBlacklistReq req){
        if(req.getDays() != null)
            stringRedisTemplate.opsForValue().set(Const.USER_PERIOD + req.getUserId(), "有限封禁", req.getDays(), TimeUnit.DAYS);
        else
            doUserPeriod(req.getUserId());
    }

    private void doUserPeriod(String userId, String reason, int days){
        stringRedisTemplate.opsForValue().set(Const.USER_PERIOD + userId, reason, days, TimeUnit.DAYS);
    }


    private void doAuthorize(String adminId){
        UserBasicInfoDTO one = this.query().eq("id", adminId).one();
        if(one == null || one.getRole() != 1){
            throw new RuntimeException("不存在此管理员");
        }
    }

    public void checkUserPeriod(String userId){
        Object o = stringRedisTemplate.opsForValue().get(Const.USER_PERIOD + userId);
        if(o != null){
            throw new RuntimeException("用户已被封禁");
        }
    }


}
