package personal.MapleChenX.lsp.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import personal.MapleChenX.lsp.common.Const;

@Configuration
public class RabbitMQConfig {

    @Bean("codeSendExchanger")
    public DirectExchange codeSendExchanger() {
        return new DirectExchange(Const.CODE_SEND_EXCHANGER, true, false);
    }

    @Bean("registerCodeQueue")
    public Queue registerCodeQueue() {
        return new Queue("registerCodeQueue", true, false, false);
    }

    @Bean
    public Binding registerCodeQueueBinding(@Qualifier("registerCodeQueue") Queue queue, @Qualifier("codeSendExchanger") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(Const.ROUTING_KEY_OF_REGISTER);
    }

    @Bean("passwordModifyCodeQueue")
    public Queue passwordModifyCodeQueue() {
        return new Queue("passwordModifyCodeQueue", true, false, false);
    }

    @Bean
    public Binding passwordModifyCodeQueueBinding(@Qualifier("passwordModifyCodeQueue") Queue queue, @Qualifier("codeSendExchanger") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(Const.ROUTING_KEY_OF_PASSWORD_MODIFY);
    }

    @Bean("emailCodeQueue")
    public Queue emailCodeQueue() {
        return new Queue("emailCodeQueue", true, false, false);
    }

    @Bean
    public Binding DefaultBinding(@Qualifier("emailCodeQueue") Queue queue, @Qualifier("codeSendExchanger") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(Const.DEFAULT_ROUTING_KEY);
    }

}
