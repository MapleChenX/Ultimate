package personal.MapleChenX.lsp.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import personal.MapleChenX.lsp.common.Const;
import personal.MapleChenX.lsp.common.model.dto.UserBasicInfoDTO;
import personal.MapleChenX.lsp.exception.CustomException;
import personal.MapleChenX.lsp.service.UserService;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtils {

    @Value("${const.default.jwt.secret_key}")
    private String key; // 秘钥

    @Value("${const.default.jwt.expire}")
    private int expire; // 令牌过期时间，小时为单位

    @Resource
    UserService userService;

    @Resource
    StringRedisTemplate redis;

    public String createJwt(UserDetails user) {
        String account = user.getUsername();
        UserBasicInfoDTO dto = userService.getUserInfoByAccount(account);
        String userId = dto.getId();
        Algorithm algorithm = Algorithm.HMAC256(key);
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", userId) // 就塞了id和account，解析的时候不要搞错了
                .withClaim("account", account)
                .withClaim("authorities", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(this.expireTime())
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire);
        return calendar.getTime();
    }

    public DecodedJWT resolveJwt(String BearerToken){
        String token = this.convertToken(BearerToken);
        if(token == null) return null;
        log.info("Token is: {}", token);
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            String tokenId = decodedJWT.getId();
            log.info("decodedJWT_Id is: {}", tokenId);
            if(this.isInvalidToken(tokenId)) return null; // token失效返回null
            Map<String, Claim> claims = decodedJWT.getClaims();
            return new Date().
                    after(claims.get("exp").asDate()) ? null : decodedJWT; // 过期返回null
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token解析失败");
        }
    }

    public boolean invalidToken(String BearerToken) {
        try {
            DecodedJWT decodedJWT = resolveJwt(BearerToken);
            String id = decodedJWT.getId();
            if (!isInvalidToken(id)) { // 如果令牌有效
                doInvalidToken(id); // 使令牌失效
                return true; // 返回true表示令牌已被使失效
            }
        }catch (RuntimeException e){
            log.error("Failed to invalidate token: {}", e.getMessage());
            throw new CustomException("不能多次退出登录");
        }
        return true;
    }

    private void doInvalidToken(String tokenId) {
        redis.opsForHash().put(Const.TOKEN_PERIOD_BUCKET, tokenId, "失效");
        // 使用日志记录，而不是抛出异常
        log.info("Token invalidated: {}", tokenId);
    }

    // 令牌是否失效(在黑名单中)
    public boolean isInvalidToken(String tokenId) {
        if(redis.opsForHash().hasKey(Const.TOKEN_PERIOD_BUCKET, tokenId)) {
            log.info("{}早已失效", tokenId);
            return true;
        }
        return false;
    }

    private String convertToken(String headerToken){
        if(headerToken == null || !headerToken.startsWith("Bearer "))
            return null;
        return headerToken.substring(7);
    }

    // 用于封装Authentication对象进行密码比对
    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("account").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class)) // 用户角色
                .build();
    }

    public String toId(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        System.out.println(claims);
        return claims.get("id").toString();
    }




}
