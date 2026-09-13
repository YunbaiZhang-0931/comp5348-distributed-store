package edu.usyd.comp5348;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Warehouse Service Application
 * Handles warehouse stock, reservations and events.
 */
@SpringBootApplication
@EnableScheduling  // 用于 OutboxPublisher 定时任务
public class WarehouseApplication {
    public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);
    }
}
