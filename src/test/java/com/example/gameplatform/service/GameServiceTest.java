package com.example.gameplatform.service;

import com.example.gameplatform.model.Game;
import com.example.gameplatform.model.GameGenre;
import com.example.gameplatform.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void getAllGames_returnsAllGames() {
        Game game = new Game();
        game.setTitle("The Witcher 3");
        when(gameRepository.findAll()).thenReturn(List.of(game));

        List<Game> result = gameService.getAllGames();

        assertEquals(1, result.size());
        assertEquals("The Witcher 3", result.get(0).getTitle());
        verify(gameRepository).findAll();
    }

    @Test
    void getGameById_existingId_returnsGame() {
        Game game = new Game();
        game.setTitle("Portal 2");
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        Game result = gameService.getGameById(1L);

        assertEquals("Portal 2", result.getTitle());
    }

    @Test
    void getGameById_notFound_throwsException() {
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> gameService.getGameById(99L));

        assertTrue(ex.getMessage().contains("Game not found"));
    }

    @Test
    void getByGenre_returnsFilteredGames() {
        Game game = new Game();
        game.setGenre(GameGenre.RPG);
        when(gameRepository.findByGenre(GameGenre.RPG)).thenReturn(List.of(game));

        List<Game> result = gameService.getByGenre(GameGenre.RPG);

        assertEquals(1, result.size());
        assertEquals(GameGenre.RPG, result.get(0).getGenre());
        verify(gameRepository).findByGenre(GameGenre.RPG);
    }

    @Test
    void getByGenre_noGames_returnsEmptyList() {
        when(gameRepository.findByGenre(GameGenre.MMORPG)).thenReturn(List.of());

        List<Game> result = gameService.getByGenre(GameGenre.MMORPG);

        assertTrue(result.isEmpty());
    }
}
