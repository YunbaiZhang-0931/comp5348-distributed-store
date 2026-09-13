package com.comp5348.bank.config;

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

    /** 定义当前 Bank 服务使用的 Exchange */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(Topics.BANK_EXCHANGE, true, false);
    }

    /** 💰 支付请求队列（Orchestrator -> Bank） */
    @Bean
    public Queue paymentRequestQueue() {
        return new Queue("bank.payment.request.queue", true);
    }

    /** 💸 退款请求队列（Orchestrator -> Bank） */
    @Bean
    public Queue refundRequestQueue() {
        return new Queue("bank.refund.request.queue", true);
    }

    /** 绑定：监听 bank.payment.request */
    @Bean
    public Binding bindingPaymentRequest(@Qualifier("paymentRequestQueue") Queue paymentRequestQueue, TopicExchange exchange) {
        return BindingBuilder.bind(paymentRequestQueue)
                .to(exchange)
                .with(Topics.BankCmd.PAYMENT_REQUEST);
    }

    /** 绑定：监听 bank.refund.request */
    @Bean
    public Binding bindingRefundRequest(@Qualifier("refundRequestQueue") Queue refundRequestQueue, TopicExchange exchange) {
        return BindingBuilder.bind(refundRequestQueue)
                .to(exchange)
                .with(Topics.BankCmd.REFUND_REQUEST);
    }
}

