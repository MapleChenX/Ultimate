package personal.MapleChenX.imTcp.test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ResourceBundle;

@Slf4j
public class ConfigLoader {
    public static ResourceBundle config;

    static {
        try {
            config = ResourceBundle.getBundle("config");
        } catch (Exception  e) {
            log.error("加载配置文件失败", e);
        }
    }

    public static String get(String key) {
        return config.getString(key);
    }

    public static void main(String[] args) throws IOException {


        String dbUrl2 = config.getString("db.url");
        String dbUsername2 = config.getString("db.username");
        String dbPassword2 = config.getString("db.password");

        System.out.println(dbUrl2);
        System.out.println(dbUsername2);
        System.out.println(dbPassword2);
    }
}
