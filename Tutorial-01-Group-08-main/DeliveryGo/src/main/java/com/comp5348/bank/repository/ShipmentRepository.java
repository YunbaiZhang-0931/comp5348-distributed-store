package com.comp5348.bank.repository;

import com.comp5348.bank.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Data Access Object for account database table.
 */
public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    Shipment findByOrderId(String orderId);
}
