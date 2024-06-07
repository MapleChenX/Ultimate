package personal.MapleChenX.lsp.common.model.resp;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class LoginResp {
    public String id; //UUID生成
    public String account; //自定义登录账号
    public String email;
    public String username; //null
    public Integer gender; //null
    public Integer role;
    public String avatar;
    public Timestamp registerTime; //需要被解析为timestamp类型
    public String token;
}
