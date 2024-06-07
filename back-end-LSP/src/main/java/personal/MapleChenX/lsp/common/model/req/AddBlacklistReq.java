package personal.MapleChenX.lsp.common.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddBlacklistReq {
    @NotBlank
    private String userId;
    @NotBlank
    private String adminId;
    @NotEmpty
    private Integer days;
}
