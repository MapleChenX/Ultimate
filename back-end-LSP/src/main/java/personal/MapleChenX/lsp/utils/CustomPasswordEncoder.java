package personal.MapleChenX.lsp.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder {

    public final BCryptPasswordEncoder encoder =new BCryptPasswordEncoder();

    /**
     * 自定义加密算法，留着以后可能会自定义吧
     */
    @Override
    public String encode(CharSequence frontEndPassword) {
        return frontEndPassword.toString();
    }

    /**
     * 匹配密码
     * @param frontEndPassword 用户输入的密码
     * @param dataBasePassword 数据库中的密码
     */
    @Override
    public boolean matches(CharSequence frontEndPassword, String dataBasePassword) { // TODO 3 比对密码
        String str = frontEndPassword.toString();
        // 参数1是加密之前的密码，参数2是加密之后的密码
        return encoder.matches(dataBasePassword, str);
    }

}
