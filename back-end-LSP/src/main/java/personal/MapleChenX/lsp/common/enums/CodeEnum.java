package personal.MapleChenX.lsp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CodeEnum {

    REGISTER(1),
    PASSWORD_MODIFY(2);


    public final int code;
}
