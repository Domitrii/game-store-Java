package com.example.gameplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Відповідь після успішної авторизації")
public class AuthResponse {

    @Schema(description = "JWT access-токен")
    private String token;

    @Schema(description = "Ідентифікатор користувача")
    private String userId;

    public AuthResponse(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public String getUserId() { return userId; }
}
