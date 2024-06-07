package personal.MapleChenX.lsp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServerEnum {

    CODE_500(500, "服务器内部错误"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "主键冲突"),
    CODE_404(404, "请求资源不存在"),

    ;


    private final int code;
    private final String msg;
}
