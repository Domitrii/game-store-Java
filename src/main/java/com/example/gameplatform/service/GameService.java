package com.example.gameplatform.service;

import com.example.gameplatform.model.Game;
import com.example.gameplatform.model.GameGenre;
import com.example.gameplatform.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private GameRepository gameRepository;

    public List<Game> getAllGames() {
        log.info("Fetching all games");
        return gameRepository.findAll();
    }

    public List<Game> getByGenre(GameGenre genre) {
        log.info("Fetching games by genre: {}", genre);
        return gameRepository.findByGenre(genre);
    }

    public Game getGameById(Long id) {
        log.info("Fetching game by id: {}", id);
        return gameRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Game not found with id: {}", id);
                    return new RuntimeException("Game not found with id: " + id);
                });
    }

    @Transactional
    public Game createGame(Game game) {
        log.info("Creating new game: {}", game.getTitle());
        return gameRepository.save(game);
    }

    @Transactional
    public Game updateGame(Long id, Game updated) {
        log.info("Updating game with id: {}", id);
        Game existing = getGameById(id);
        existing.setTitle(updated.getTitle());
        existing.setGenre(updated.getGenre());
        existing.setPlatform(updated.getPlatform());
        existing.setPrice(updated.getPrice());
        existing.setPublisher(updated.getPublisher());
        existing.setReleaseDate(updated.getReleaseDate());
        existing.setShortDescription(updated.getShortDescription());
        existing.setThumbnail(updated.getThumbnail());
        return gameRepository.save(existing);
    }

    @Transactional
    public void deleteGame(Long id) {
        log.info("Deleting game with id: {}", id);
        if (!gameRepository.existsById(id)) {
            log.warn("Cannot delete — game not found with id: {}", id);
            throw new RuntimeException("Game not found with id: " + id);
        }
        gameRepository.deleteById(id);
    }
}