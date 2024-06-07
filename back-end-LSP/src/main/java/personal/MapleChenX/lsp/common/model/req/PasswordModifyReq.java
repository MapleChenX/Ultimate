package personal.MapleChenX.lsp.common.model.req;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordModifyReq {
    @Length(max = 50, min = 6)
    private String oldPassword;
    @Length(max = 50, min = 6)
    private String newPassword;
    @Email
    private String email;
    @Length(min = 6, max = 6)
    private String code;
}
