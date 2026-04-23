package com.example.gameplatform.service;

import com.example.gameplatform.model.*;
import com.example.gameplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private GameRepository gameRepository;

    public Purchase buyGame(String userId, Long gameId) {
        if (purchaseRepository.existsByUserIdAndGameId(userId, gameId)) {
            throw new RuntimeException("Already purchased"); // TC-18 [cite: 285]
        }
        
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new RuntimeException("Game not found")); // TC-20 [cite: 285]
            
        Purchase p = new Purchase();
        p.setUserId(userId);
        p.setGameId(gameId);
        p.setPrice(game.getPrice()); 
        return purchaseRepository.save(p);
    }
}