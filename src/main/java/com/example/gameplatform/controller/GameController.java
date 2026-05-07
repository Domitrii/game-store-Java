package com.example.gameplatform.controller;

import com.example.gameplatform.model.Game;
import com.example.gameplatform.model.GameGenre;
import com.example.gameplatform.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@Tag(name = "Ігри", description = "Перегляд каталогу ігор")
public class GameController {

    @Autowired
    private GameService gameService;

    @Operation(summary = "Отримати всі ігри",
               description = "Повертає повний список ігор з каталогу. Доступно без авторизації.")
    @ApiResponse(responseCode = "200", description = "Список ігор",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Game.class))))
    @GetMapping
    public List<Game> getAllGames() {
        return gameService.getAllGames();
    }

    @Operation(summary = "Отримати гру за ID",
               description = "Повертає детальну інформацію про конкретну гру.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Гра знайдена",
                    content = @Content(schema = @Schema(implementation = Game.class))),
            @ApiResponse(responseCode = "404", description = "Гру не знайдено",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(
            @Parameter(description = "ID гри", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGameById(id));
    }

    @Operation(summary = "Отримати ігри за жанром",
               description = "Фільтрує каталог за вказаним жанром.")
    @ApiResponse(responseCode = "200", description = "Список ігор обраного жанру",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Game.class))))
    @GetMapping("/genre/{genre}")
    public List<Game> getByGenre(
            @Parameter(description = "Жанр гри", example = "ACTION") @PathVariable GameGenre genre) {
        return gameService.getByGenre(genre);
    }
}
