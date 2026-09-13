package edu.usyd.comp5348.repository;


import edu.usyd.comp5348.domain.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockLevelRepo extends JpaRepository<StockLevel, String> {
    List<StockLevel> findByItemId(String itemId);
}
