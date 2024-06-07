package personal.MapleChenX.lsp.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import personal.MapleChenX.lsp.common.enums.Enum4CodeAndMsg2CustomException4Service;


@NoArgsConstructor
@Setter
@Getter
public class CustomException extends RuntimeException {
    Integer code;
    String message;
    Enum4CodeAndMsg2CustomException4Service codeEnum;

    public CustomException(String message) {
        super(message);
        this.message = message;
    }

    public CustomException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CustomException(Enum4CodeAndMsg2CustomException4Service codeEnum) {
        super(codeEnum.getMsg());
        this.codeEnum = codeEnum;
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMsg();
    }

    public CustomException(Throwable e) {
        super(e);
    }

    public CustomException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    /**
     * 重写fillInStackTrace 业务异常不需要堆栈信息，提高效率.
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
