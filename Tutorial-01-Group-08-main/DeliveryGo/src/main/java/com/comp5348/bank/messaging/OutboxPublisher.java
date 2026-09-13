package com.comp5348.bank.messaging;

import com.comp5348.bank.model.Outbox;
import com.comp5348.bank.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepo;
    private final RabbitTemplate amqp;

    @Qualifier("exchange")
    private final TopicExchange exchange;

    /** 每500ms扫描未发送事件并发布 */
    @Scheduled(fixedDelay = 500)
    @Transactional
    public void publish() {
        outboxRepo.findAll().stream()
                .filter(o -> o.getStatus() == Outbox.Status.NEW)
                .forEach(o -> {
                    try {
                        var props = new MessageProperties();
                        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                        var msg = new Message(o.getPayload().getBytes(), props);
                        amqp.send(exchange.getName(), o.getType(), msg);
                        o.setStatus(Outbox.Status.SENT);
                        outboxRepo.save(o);
                        System.out.println("Sent event: " + o.getType());
                    } catch (Exception e) {
                        System.err.println("❌ Failed to send event " + o.getType() + ": " + e.getMessage());
                    }
                });
    }
}

