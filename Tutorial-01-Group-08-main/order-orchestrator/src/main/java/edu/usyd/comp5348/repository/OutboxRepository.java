package edu.usyd.comp5348.repository;

import edu.usyd.comp5348.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, String> {}
