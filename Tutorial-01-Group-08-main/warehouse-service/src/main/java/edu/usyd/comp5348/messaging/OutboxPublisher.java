package edu.usyd.comp5348.messaging;

import edu.usyd.comp5348.domain.Outbox;
import edu.usyd.comp5348.repository.OutboxRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepo outbox;
    private final RabbitTemplate amqp;
    private final TopicExchange exchange;

    /** 每500ms扫描一次未发送事件并发布到 MQ */
    @Scheduled(fixedDelay = 500)
    @Transactional
    public void publish() {
        outbox.findAll().stream()
                .filter(o -> o.getStatus() == Outbox.Status.NEW)
                .forEach(o -> {
                    var props = new MessageProperties();
                    props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                    var msg = new Message(o.getPayload().getBytes(), props);

                    amqp.send(exchange.getName(), o.getType(), msg);
                    o.setStatus(Outbox.Status.SENT);
                    outbox.save(o);
                });
    }
}
