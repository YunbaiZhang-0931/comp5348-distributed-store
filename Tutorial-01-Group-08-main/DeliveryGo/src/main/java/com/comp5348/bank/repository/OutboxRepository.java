package com.comp5348.bank.repository;


/**
 * Data Access Object for account database table.
 */
import com.comp5348.bank.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, String> { }

