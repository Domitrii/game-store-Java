package com.example.gameplatform.repository;

import com.example.gameplatform.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, String> {
    boolean existsByUserIdAndGameId(String userId, Long gameId);
}