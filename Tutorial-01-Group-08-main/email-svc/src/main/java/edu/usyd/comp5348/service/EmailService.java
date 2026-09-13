package edu.usyd.comp5348.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void notify(String to, String orderId, String content) {
        log.info("📨 Simulate sending an email to [{}], subject: Order #{} Notification, content: {}", to, orderId, content);
    }
}

