package com.comp5348.bank.service;

import com.comp5348.bank.model.Account;
import com.comp5348.bank.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final AccountRepository accountRepository;

    /**
     * 应用启动后执行，若 Account 表为空，则自动插入初始账户
     */
    @PostConstruct
    public void initAccounts() {
        long count = accountRepository.count();
        if (count == 0) {
            Account customer = new Account();
            customer.setName("Customer Account");
            customer.setBalance(BigDecimal.valueOf(1000.00));

            Account store = new Account();
            store.setName("Store Account");
            store.setBalance(BigDecimal.ZERO);

            accountRepository.save(customer);
            accountRepository.save(store);

            System.out.println("Initialized default accounts.");
        } else {
            System.out.println("Accounts already exist, skipping initialization.");
        }
    }
}
