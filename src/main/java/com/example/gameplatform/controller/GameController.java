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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@Tag(name = "Ігри", description = "Перегляд та управління каталогом ігор")
public class GameController {

    @Autowired
    private GameService gameService;

    // ─── READ ────────────────────────────────────────────────────────────────

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
            @ApiResponse(responseCode = "404", description = "Гру не знайдено", content = @Content)
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

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @Operation(summary = "Додати нову гру", description = "Додає гру до каталогу. Потребує JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Гру додано",
                    content = @Content(schema = @Schema(implementation = Game.class))),
            @ApiResponse(responseCode = "400", description = "Невалідні дані", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизовано", content = @Content)
    })
    @SecurityRequirement(name = "Bearer JWT")
    @PostMapping
    public ResponseEntity<Game> createGame(@Valid @RequestBody Game game) {
        return ResponseEntity.status(201).body(gameService.createGame(game));
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    @Operation(summary = "Оновити гру", description = "Оновлює дані існуючої гри. Потребує JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Гру оновлено",
                    content = @Content(schema = @Schema(implementation = Game.class))),
            @ApiResponse(responseCode = "400", description = "Невалідні дані", content = @Content),
            @ApiResponse(responseCode = "404", description = "Гру не знайдено", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизовано", content = @Content)
    })
    @SecurityRequirement(name = "Bearer JWT")
    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(
            @Parameter(description = "ID гри") @PathVariable Long id,
            @Valid @RequestBody Game game) {
        return ResponseEntity.ok(gameService.updateGame(id, game));
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @Operation(summary = "Видалити гру", description = "Видаляє гру з каталогу. Потребує JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Гру видалено", content = @Content),
            @ApiResponse(responseCode = "404", description = "Гру не знайдено", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизовано", content = @Content)
    })
    @SecurityRequirement(name = "Bearer JWT")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(
            @Parameter(description = "ID гри") @PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
