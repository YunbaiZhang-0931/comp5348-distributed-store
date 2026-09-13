package edu.usyd.comp5348.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usyd.comp5348.Topics;
import edu.usyd.comp5348.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class EmailListener {

    private final ObjectMapper om;
    private final EmailService emailService;

    @RabbitListener(queues = "email.notifications.queue")
    public void onMessage(Message msg) {
        try {
            String rk = msg.getMessageProperties().getReceivedRoutingKey();
            JsonNode evt = om.readTree(new String(msg.getBody()));
            String orderId = evt.path("orderId").asText();
            String email = evt.path("email").asText("customer@example.com"); // mock email

            switch (rk) {
                case Topics.DeliveryEvt.PICKED_UP ->
                        emailService.notify(email, orderId, "📦 Your package has been picked up by the courier company.");
                case Topics.DeliveryEvt.OUT_FOR_DELIVERY ->
                        emailService.notify(email, orderId, "🚚 Your package is being delivered.");
                case Topics.DeliveryEvt.DELIVERED ->
                        emailService.notify(email, orderId, "✅ Your package has been delivered!");
                case Topics.BankEvt.PAYMENT_FAILED ->
                    emailService.notify(email, orderId, "Your payment has failed!");
                case Topics.BankEvt.REFUNDED ->
                        emailService.notify(email, orderId, "💰 Your order has been refunded.");
                case Topics.StoreEvt.ORDER_CANCEL_REQUEST ->
                        emailService.notify(email, orderId, "❌ Your order has been cancelled, and the refund is being processed.");
                default ->
                        log.warn("⚠️ [EmailListener] Unrecognized event: {}", rk);
            }

        } catch (Exception e) {
            log.error("❌ [EmailListener] Failed", e);
        }
    }
}

