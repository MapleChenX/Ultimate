package personal.MapleChenX.lsp.common.model.ret;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

@Data
public class ResponseTemplate<T> {
    private int code;
    private String message;
    private T data;

    public ResponseTemplate() {
    }

    public ResponseTemplate(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseTemplate<T> success(T data){
        return new ResponseTemplate<>(200, "请求成功", data);
    }

    public static <T> ResponseTemplate<T> success(){
        return new ResponseTemplate<>(200, "请求成功", null);
    }

    public static <T> ResponseTemplate<T> failure(int code, String message){
        return new ResponseTemplate<>(code, message, null);
    }

    public static <T> ResponseTemplate<T> unauthorized(){
        return new ResponseTemplate<>(401, "未认证用户哦", null);
    }

    public static <T> ResponseTemplate<T> forbidden(){
        return new ResponseTemplate<>(403, "小子，你早已被BAN", null);
    }



    public static <T> ResponseTemplate<T> forbidden(String msg){
        return new ResponseTemplate<>(403, msg, null);
    }

    public String toJsonString(){
        return JSON.toJSONString(this);
    }
}
