package edu.usyd.comp5348.store_api.config;


import edu.usyd.comp5348.Topics;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 仅声明 Exchange（队列由各消费服务声明） */
import org.springframework.amqp.core.*;

/**
 * RabbitMQ 配置：
 * Store 监听所有 order.* 事件（异步状态更新）
 */
@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange storeExchange() {
        return new TopicExchange(Topics.STORE_EXCHANGE, true, false);
    }

    @Bean
    public Queue storeOrderEventsQueue() {
        return new Queue("store.order.events.queue", true);
    }

    @Bean
    public Binding bindingStoreEvents(Queue storeOrderEventsQueue, TopicExchange storeExchange) {
        // 绑定所有 order.xxx 类型的事件
        return BindingBuilder.bind(storeOrderEventsQueue)
                .to(storeExchange)
                .with("order.*");
    }
}
