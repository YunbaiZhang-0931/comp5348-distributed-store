package edu.usyd.comp5348.store_api.service;

import edu.usyd.comp5348.store_api.domain.Item;
import edu.usyd.comp5348.store_api.repo.ItemRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** 启动时初始化演示账号与示例商品 */
@Component
public class InitDataRunner implements CommandLineRunner {
    private final AuthService authService;
    private final ItemRepo itemRepo;

    public InitDataRunner(AuthService authService, ItemRepo itemRepo) {
        this.authService = authService;
        this.itemRepo = itemRepo;
    }

    @Override
    public void run(String... args) {
        authService.ensureDemoUser();

        if (itemRepo.count() == 0) {
            itemRepo.save(Item.builder()
                    .id("ITEM-001")
                    .name("USB-C Cable")
                    .unitPrice(new BigDecimal("19.99"))
                    .enabled(true)
                    .build());

            itemRepo.save(Item.builder()
                    .id("ITEM-002")
                    .name("Wireless Mouse")
                    .unitPrice(new BigDecimal("29.90"))
                    .enabled(true)
                    .build());

            itemRepo.save(Item.builder()
                    .id("ITEM-003")
                    .name("Keyboard")
                    .unitPrice(new BigDecimal("49.00"))
                    .enabled(true)
                    .build());

            System.out.println("Initialized default items with fixed IDs.");
        } else {
            System.out.println("Items already exist, skipping initialization.");
        }
    }

}
