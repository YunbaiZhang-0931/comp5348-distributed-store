package edu.usyd.comp5348.messaging;

import edu.usyd.comp5348.entity.Outbox;
import edu.usyd.comp5348.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {

    private final OutboxRepository outboxRepo;
    private final RabbitTemplate amqp;
    private final TopicExchange warehouseExchange;
    private final TopicExchange bankExchange;
    private final TopicExchange deliveryExchange;
    private final TopicExchange storeExchange;

    public OutboxPublisher(
            OutboxRepository outboxRepo,
            RabbitTemplate amqp,
            @Qualifier("storeExchange") TopicExchange storeExchange,
            @Qualifier("warehouseExchange") TopicExchange warehouseExchange,
            @Qualifier("bankExchange") TopicExchange bankExchange,
            @Qualifier("deliveryExchange") TopicExchange deliveryExchange) {
        this.outboxRepo = outboxRepo;
        this.amqp = amqp;
        this.warehouseExchange = warehouseExchange;
        this.bankExchange = bankExchange;
        this.deliveryExchange = deliveryExchange;
        this.storeExchange = storeExchange;
    }

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void publish() {
        outboxRepo.findAll().stream()
                .filter(o -> o.getStatus() == Outbox.Status.NEW)
                .forEach(o -> {
                    TopicExchange target = route(o.getType());
                    var props = new MessageProperties();
                    props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                    amqp.send(target.getName(), o.getType(), new Message(o.getPayload().getBytes(), props));
                    o.setStatus(Outbox.Status.SENT);
                    outboxRepo.save(o);
                });
    }

    private TopicExchange route(String routingKey) {
        if (routingKey.startsWith("warehouse.")) return warehouseExchange;
        if (routingKey.startsWith("bank.")) return bankExchange;
        if (routingKey.startsWith("delivery.")) return deliveryExchange;
        if (routingKey.startsWith("order.")) return storeExchange;
        throw new IllegalArgumentException("Unknown routing key: " + routingKey);
    }
}



