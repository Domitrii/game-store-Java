package com.example.gameplatform.service;

import com.example.gameplatform.model.*;
import com.example.gameplatform.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private GameRepository gameRepository;

    @Transactional
    public Purchase buyGame(String userId, Long gameId) {
        log.info("User {} attempting to purchase game {}", userId, gameId);

        if (purchaseRepository.existsByUserIdAndGameId(userId, gameId)) {
            log.warn("User {} already purchased game {}", userId, gameId);
            throw new RuntimeException("Already purchased");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> {
                    log.warn("Game not found with id: {}", gameId);
                    return new RuntimeException("Game not found with id: " + gameId);
                });

        Purchase p = new Purchase();
        p.setUserId(userId);
        p.setGameId(gameId);
        p.setPrice(game.getPrice());
        log.info("Purchase successful — user: {}, game: {}, price: {}", userId, gameId, game.getPrice());
        return purchaseRepository.save(p);
    }
}