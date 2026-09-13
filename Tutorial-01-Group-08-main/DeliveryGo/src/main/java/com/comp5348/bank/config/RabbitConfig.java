package com.comp5348.bank.config;


import edu.usyd.comp5348.Topics;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(Topics.DELIVERY_EXCHANGE, true, false);
    }
}