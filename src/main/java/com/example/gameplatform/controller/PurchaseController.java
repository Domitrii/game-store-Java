package com.example.gameplatform.controller;

import com.example.gameplatform.model.Purchase;
import com.example.gameplatform.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<Purchase> purchase(@RequestParam String userId, @RequestParam Long gameId) {
        return ResponseEntity.status(201).body(purchaseService.buyGame(userId, gameId));
    }
}