package edu.usyd.comp5348.config;

import edu.usyd.comp5348.Topics;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(Topics.WAREHOUSE_EXCHANGE, true, false);
    }

    @Bean
    public Queue checkQueue() {
        return new Queue("warehouse.check.request.queue", true);
    }

    @Bean
    public Queue reserveQueue() {
        return new Queue("warehouse.reserve.request.queue", true);
    }

    @Bean
    public Queue releaseQueue() {
        return new Queue("warehouse.release.request.queue", true);
    }

    @Bean
    public Queue finalizeQueue() {
        return new Queue("warehouse.finalize.request.queue", true);
    }

    // 明确指定要绑定的 Queue
    @Bean
    public Binding bindingReserveRequest(@Qualifier("reserveQueue") Queue reserveQueue, TopicExchange exchange) {
        return BindingBuilder.bind(reserveQueue).to(exchange).with(Topics.WarehouseCmd.RESERVE_REQUEST);
    }

    @Bean
    public Binding bindingCheckRequest(@Qualifier("checkQueue") Queue reserveQueue, TopicExchange exchange) {
        return BindingBuilder.bind(reserveQueue).to(exchange).with(Topics.WarehouseCmd.CHECK_REQUEST);
    }


    @Bean
    public Binding bindingRelease(@Qualifier("releaseQueue") Queue releaseQueue, TopicExchange exchange) {
        return BindingBuilder.bind(releaseQueue).to(exchange).with(Topics.WarehouseCmd.RELEASE_REQUEST);
    }

    @Bean
    public Binding bindingFinalize(@Qualifier("finalizeQueue") Queue finalizeQueue, TopicExchange exchange) {
        return BindingBuilder.bind(finalizeQueue).to(exchange).with(Topics.WarehouseCmd.FINALIZE_REQUEST);
    }
}

