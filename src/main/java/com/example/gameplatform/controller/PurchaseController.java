package com.example.gameplatform.controller;

import com.example.gameplatform.model.Purchase;
import com.example.gameplatform.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase")
@Tag(name = "Покупки", description = "Придбання ігор. Потребує JWT-авторизації.")
@SecurityRequirement(name = "Bearer JWT")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Operation(summary = "Придбати гру",
               description = "Купує гру для авторизованого користувача. userId береться з JWT-токена.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Гру успішно придбано",
                    content = @Content(schema = @Schema(implementation = Purchase.class))),
            @ApiResponse(responseCode = "400", description = "Гру вже куплено",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизовано — надішліть Bearer-токен",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Гру не знайдено",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Purchase> purchase(
            @Parameter(description = "ID гри", example = "1") @RequestParam Long gameId,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.status(201).body(purchaseService.buyGame(userId, gameId));
    }
}
