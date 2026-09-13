package edu.usyd.comp5348.config;

import edu.usyd.comp5348.Topics;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

@Configuration
public class RabbitConfig {

    @Bean public TopicExchange storeExchange()     { return new TopicExchange(Topics.STORE_EXCHANGE, true, false); }
    @Bean public TopicExchange warehouseExchange() { return new TopicExchange(Topics.WAREHOUSE_EXCHANGE, true, false); }
    @Bean public TopicExchange bankExchange()      { return new TopicExchange(Topics.BANK_EXCHANGE, true, false); }
    @Bean public TopicExchange deliveryExchange()  { return new TopicExchange(Topics.DELIVERY_EXCHANGE, true, false); }

    @Bean public Queue orchEventsQueue() { return new Queue("orchestrator.events", true); }

    // 🏪 Store
    @Bean
    public Binding bindStoreCreated(@Qualifier("orchEventsQueue") Queue q,
                                    @Qualifier("storeExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.StoreEvt.ORDER_CREATED);
    }

    @Bean
    public Binding bindStoreCancel(@Qualifier("orchEventsQueue") Queue q,
                                   @Qualifier("storeExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.StoreEvt.ORDER_CANCEL_REQUEST);
    }


    // 🏭 Warehouse
    @Bean
    public Binding bindWhReserved(@Qualifier("orchEventsQueue") Queue q,
                                  @Qualifier("warehouseExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.WarehouseEvt.RESERVED);
    }

    @Bean Binding bindWhOutOfStock(@Qualifier("orchEventsQueue") Queue q,
                                   @Qualifier("warehouseExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.WarehouseEvt.OUT_OF_STOCK);
    }

    @Bean
    public Binding bindWhReleased(@Qualifier("orchEventsQueue") Queue q,
                                  @Qualifier("warehouseExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.WarehouseEvt.RELEASED);
    }

    @Bean
    public Binding bindWhFinalized(@Qualifier("orchEventsQueue") Queue q,
                                   @Qualifier("warehouseExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.WarehouseEvt.FINALIZED);
    }

    // 🏦 Bank
    @Bean
    public Binding bindBankPaid(@Qualifier("orchEventsQueue") Queue q,
                                @Qualifier("bankExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.BankEvt.PAID);
    }

    @Bean
    public Binding bindBankFailed(@Qualifier("orchEventsQueue") Queue q,
                                  @Qualifier("bankExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.BankEvt.PAYMENT_FAILED);
    }

    @Bean
    public Binding bindBankRefunded(@Qualifier("orchEventsQueue") Queue q,
                                    @Qualifier("bankExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.BankEvt.REFUNDED);
    }

    // 🚚 Delivery
    @Bean
    public Binding bindDelivReceived(@Qualifier("orchEventsQueue") Queue q,
                                     @Qualifier("deliveryExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.DeliveryEvt.RECEIVED);
    }

    @Bean
    public Binding bindDelivPicked(@Qualifier("orchEventsQueue") Queue q,
                                   @Qualifier("deliveryExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.DeliveryEvt.PICKED_UP);
    }

    @Bean
    public Binding bindDelivOFD(@Qualifier("orchEventsQueue") Queue q,
                                @Qualifier("deliveryExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.DeliveryEvt.OUT_FOR_DELIVERY);
    }

    @Bean
    public Binding bindDelivDelivered(@Qualifier("orchEventsQueue") Queue q,
                                      @Qualifier("deliveryExchange") TopicExchange x) {
        return BindingBuilder.bind(q).to(x).with(Topics.DeliveryEvt.DELIVERED);
    }
}

