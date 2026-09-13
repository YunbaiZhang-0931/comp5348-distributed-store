package com.comp5348.bank.repository;

import com.comp5348.bank.model.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    TransactionRecord findByOrderNo(String orderNo);

}

