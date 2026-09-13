package edu.usyd.comp5348.repository;


import edu.usyd.comp5348.domain.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockReservationRepo extends JpaRepository<StockReservation, String> {
    List<StockReservation> findByOrderId(String orderId);
}
