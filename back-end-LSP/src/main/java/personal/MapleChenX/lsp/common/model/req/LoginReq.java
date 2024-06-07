package personal.MapleChenX.lsp.common.model.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginReq {
    @Size(min = 1, max = 50, message = "账号长度不正确")
    private String account; //自定义登录账号
    @Size(min = 6, max = 50, message = "密码长度不正确")
    private String password;
    @Email
    private String email; //支持邮箱和账号登录 todo 这里可能出现问题
}
