package personal.MapleChenX.lsp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Enum4CodeAndMsg2CustomException4Service {

    CODE_250(250, "业务异常");

    private final int code;
    private final String msg;

}
