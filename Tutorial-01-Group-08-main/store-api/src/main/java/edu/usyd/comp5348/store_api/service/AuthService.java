package edu.usyd.comp5348.store_api.service;

import edu.usyd.comp5348.store_api.domain.Customer;
import edu.usyd.comp5348.store_api.repo.CustomerRepo;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final CustomerRepo customers;
    public AuthService(CustomerRepo customers){ this.customers = customers; }

    // 初始化演示账户（可放在 CommandLineRunner）
    public void ensureDemoUser() {
        if(!customers.existsByUsername("customer")) {
            var hash = BCrypt.hashpw("COMP5348", BCrypt.gensalt());
            customers.save(Customer.builder().username("customer").passwordHash(hash).email("customer@example.com").build());
        }
    }

    public Customer login(String username, String password){
        var c = customers.findByUsername(username);
        if(c.isPresent() && BCrypt.checkpw(password, c.get().getPasswordHash())) return c.get();
        throw new RuntimeException("Invalid credentials");
    }
}
