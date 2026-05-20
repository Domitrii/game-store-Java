package com.example.gameplatform.service;

import com.example.gameplatform.model.Game;
import com.example.gameplatform.model.Purchase;
import com.example.gameplatform.repository.GameRepository;
import com.example.gameplatform.repository.PurchaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void buyGame_success_returnsPurchaseWithCorrectData() {
        Game game = new Game();
        game.setPrice(29.99);

        when(purchaseRepository.existsByUserIdAndGameId("user-1", 1L)).thenReturn(false);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(i -> i.getArgument(0));

        Purchase result = purchaseService.buyGame("user-1", 1L);

        assertEquals("user-1", result.getUserId());
        assertEquals(1L, result.getGameId());
        assertEquals(29.99, result.getPrice());
        verify(purchaseRepository).save(any(Purchase.class));
    }

    @Test
    void buyGame_alreadyPurchased_throwsException() {
        when(purchaseRepository.existsByUserIdAndGameId("user-1", 1L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> purchaseService.buyGame("user-1", 1L));

        assertEquals("Already purchased", ex.getMessage());
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void buyGame_gameNotFound_throwsException() {
        when(purchaseRepository.existsByUserIdAndGameId("user-1", 99L)).thenReturn(false);
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> purchaseService.buyGame("user-1", 99L));

        assertTrue(ex.getMessage().contains("Game not found"));
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void buyGame_priceIsCorrectlyCopiedFromGame() {
        Game game = new Game();
        game.setPrice(59.99);

        when(purchaseRepository.existsByUserIdAndGameId("user-2", 3L)).thenReturn(false);
        when(gameRepository.findById(3L)).thenReturn(Optional.of(game));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(i -> i.getArgument(0));

        Purchase result = purchaseService.buyGame("user-2", 3L);

        assertEquals(59.99, result.getPrice(), 0.001);
    }
}
