package com.example.gameplatform.repository;

import com.example.gameplatform.model.Game;
import com.example.gameplatform.model.GameGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByGenre(GameGenre genre);
}