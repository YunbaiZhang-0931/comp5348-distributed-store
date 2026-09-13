package edu.usyd.comp5348.store_api.repo;

import edu.usyd.comp5348.store_api.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** 商品表（演示可只放少量商品） */
public interface ItemRepo extends JpaRepository<Item, String> {
    List<Item> findByEnabledTrueOrderByNameAsc();
}
