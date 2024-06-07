package personal.MapleChenX.lsp.receiver;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import personal.MapleChenX.lsp.common.model.mid.EmailAndCode;

@Component
public class RabbitMQReceiver {

    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitListener(queues = "registerCodeQueue")
    public void receiveRegisterCode(String message) {
        // 将message解包为EmailAndCode对象
        EmailAndCode emailAndCode = JSON.parseObject(message, EmailAndCode.class);
        // 发送邮件
        sendMailMessage4Register(emailAndCode);
    }

    @RabbitListener(queues = "passwordModifyCodeQueue")
    public void receivePasswordModifyCode(String message) {
        // 将message解包为EmailAndCode对象
        EmailAndCode emailAndCode = JSON.parseObject(message, EmailAndCode.class);
        // 发送邮件
        sendMailMessage4PasswordModify(emailAndCode);
    }

    @RabbitListener(queues = "emailCodeQueue")
    public void defaultQueue(String message) {
        // 将message解包为EmailAndCode对象
        EmailAndCode emailAndCode = JSON.parseObject(message, EmailAndCode.class);
        // 发送邮件
        Maybe(emailAndCode);
    }

    private void sendMailMessage4Register(EmailAndCode emailAndCode){
        // 发送邮件
        String email = emailAndCode.getEmail();
        int code = emailAndCode.getCode();
        SimpleMailMessage message = createMessage("欢迎注册本网站",
                "您的邮件注册验证码为: "+code+"，有效时间3分钟，为了保障您的账户安全，请勿向他人泄露验证码信息。",
                email);
        sender.send(message);
    }

    private void sendMailMessage4PasswordModify(EmailAndCode emailAndCode){
        // 发送邮件
        String email = emailAndCode.getEmail();
        int code = emailAndCode.getCode();
        SimpleMailMessage message = createMessage("密码忘了哈",
                "您的密码修改验证码为: "+code+"，有效时间3分钟，为了保障您的账户安全，请勿向他人泄露验证码信息。",
                email);
        sender.send(message);
    }

    private void Maybe(EmailAndCode emailAndCode){
        // 发送邮件
        String email = emailAndCode.getEmail();
        int code = emailAndCode.getCode();
        SimpleMailMessage message = createMessage("不知道为什么会发这个邮件",
                "也许你想要？: "+code+"，有效时间3分钟。",
                email);
        sender.send(message);
    }

    /**
     * 快速封装简单邮件消息实体
     * @param title 标题
     * @param content 内容
     * @param email 收件人
     * @return 邮件实体
     */
    private SimpleMailMessage createMessage(String title, String content, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
