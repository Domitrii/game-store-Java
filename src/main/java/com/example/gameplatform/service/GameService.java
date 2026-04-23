package com.example.gameplatform.service;

import com.example.gameplatform.model.Game;
import com.example.gameplatform.model.GameGenre;
import com.example.gameplatform.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    public List<Game> getAllGames() { return gameRepository.findAll(); }
    
    public List<Game> getByGenre(GameGenre genre) { return gameRepository.findByGenre(genre); }

    public Game getGameById(Long id) {
        return gameRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
    }
}