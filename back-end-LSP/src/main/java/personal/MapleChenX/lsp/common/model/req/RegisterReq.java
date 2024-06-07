package personal.MapleChenX.lsp.common.model.req;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class RegisterReq {
    @Email
    String email;
    @Length(max = 50, min = 1)
    String account; //自定义账号
    @Length(max = 50, min = 6)
    String password;
    @Length(max = 6, min = 6)
    String code;
}
