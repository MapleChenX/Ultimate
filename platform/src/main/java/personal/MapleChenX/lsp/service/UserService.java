package personal.MapleChenX.lsp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import personal.MapleChenX.lsp.common.model.dto.UserBasicInfoDTO;
import personal.MapleChenX.lsp.common.model.req.AddBlacklistReq;
import personal.MapleChenX.lsp.common.model.req.LoginReq;
import personal.MapleChenX.lsp.common.model.req.PasswordModifyReq;
import personal.MapleChenX.lsp.common.model.req.RegisterReq;
import personal.MapleChenX.lsp.common.model.ret.ResponseTemplate;

public interface UserService extends IService<UserBasicInfoDTO> {
    public ResponseTemplate<?> register(RegisterReq req, String code);

    public ResponseTemplate<?> sendCode(String email, int type);

    public ResponseTemplate<?> login(LoginReq req);

    public UserBasicInfoDTO getUserInfoByAccount(String account);

    public ResponseTemplate<?> addBlacklist(AddBlacklistReq req);

    public ResponseTemplate<?> passwordModify(PasswordModifyReq req, String code);

    public void updateAvatar(String avatar, String uid);

}
