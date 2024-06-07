package personal.MapleChenX.lsp.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@TableName("user_basic_info")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserBasicInfoDTO {
    private String id; //UUID生成
    private String account; //自定义登录账号
    private String password;
    private String email;
    private String username; //null
    private Integer gender; //null
    private Integer role;
    private String avatar;
    private Timestamp registerTime; //需要被解析为timestamp类型

}
