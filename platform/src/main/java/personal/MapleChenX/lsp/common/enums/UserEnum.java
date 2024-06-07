package personal.MapleChenX.lsp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 9开头的都和用户有关
@Getter
@AllArgsConstructor
public enum UserEnum {

    VERIFY_CODE_EXPIRED(901, "验证码已过期"),
    VERIFY_CODE_NOT_EXIST(902, "验证码错误"),
    EMAIL_IN_USE(903, "邮箱已被注册"),
    USERNAME_IN_USE(904, "用户名已被注册"),
    ACCOUNT_NOT_EXIST(905, "账号不存在"),
    PASSWORD_NOT_RIGHT(906, "密码错误"),

    ;

    private final int code;
    private final String msg;

}
