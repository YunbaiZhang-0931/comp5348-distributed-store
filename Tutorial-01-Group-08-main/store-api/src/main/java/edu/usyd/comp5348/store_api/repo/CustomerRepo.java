package edu.usyd.comp5348.store_api.repo;

import edu.usyd.comp5348.store_api.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** 用户表的 CRUD 与常用查询 */
public interface CustomerRepo extends JpaRepository<Customer, String> {
    boolean existsByUsername(String username);
    Optional<Customer> findByUsername(String username);
}
