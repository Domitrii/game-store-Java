package com.example.gameplatform.controller;

import com.example.gameplatform.config.JwtService;
import com.example.gameplatform.dto.AuthResponse;
import com.example.gameplatform.dto.LoginRequest;
import com.example.gameplatform.model.User;
import com.example.gameplatform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Авторизація", description = "Реєстрація та вхід користувачів через JWT")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Operation(summary = "Реєстрація нового користувача",
               description = "Створює акаунт. Пароль зберігається у вигляді BCrypt-хешу.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Користувача зареєстровано",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "409", description = "Email вже використовується",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return ResponseEntity.status(201).body(userService.register(user));
    }

    @Operation(summary = "Вхід в систему",
               description = "Повертає JWT-токен. Використовуйте його в заголовку `Authorization: Bearer <token>`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успішний вхід — повертає токен",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Невірний email або пароль",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return userService.findByEmail(request.getEmail())
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPasswordHash()))
                .map(u -> {
                    try {
                        String token = jwtService.generateToken(u.getId(), u.getEmail());
                        return ResponseEntity.ok(new AuthResponse(token, u.getId()));
                    } catch (Exception e) {
                        throw new RuntimeException("Token generation failed", e);
                    }
                })
                .orElse(ResponseEntity.status(401).build());
    }
}
