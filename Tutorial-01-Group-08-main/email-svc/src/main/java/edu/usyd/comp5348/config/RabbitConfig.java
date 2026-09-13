package edu.usyd.comp5348.config;

import edu.usyd.comp5348.Topics;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue emailQueue() {
        return new Queue("email.notifications.queue", true);
    }

    @Bean
    public TopicExchange deliveryExchange() {
        return new TopicExchange(Topics.DELIVERY_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange storeExchange() {
        return new TopicExchange(Topics.STORE_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange bankExchange() {
        return new TopicExchange(Topics.BANK_EXCHANGE, true, false);
    }

    // ✅ 指定注入 deliveryExchange
    @Bean
    public Binding bindDeliveryEvents(Queue q, @Qualifier("deliveryExchange") TopicExchange deliveryExchange) {
        return BindingBuilder.bind(q).to(deliveryExchange).with("delivery.*");
    }

    // ✅ 指定注入 storeExchange
    @Bean
    public Binding bindStoreEvents(Queue q, @Qualifier("storeExchange") TopicExchange storeExchange) {
        return BindingBuilder.bind(q).to(storeExchange).with("order.cancel.request");
    }

    // ✅ 指定注入 bankExchange
    @Bean
    public Binding bindBankEvents(Queue q, @Qualifier("bankExchange") TopicExchange bankExchange) {
        return BindingBuilder.bind(q).to(bankExchange).with("order.refunded");
    }
}


