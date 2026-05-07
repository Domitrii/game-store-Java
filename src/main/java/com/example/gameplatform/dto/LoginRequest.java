package com.example.gameplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Дані для входу в систему")
public class LoginRequest {

    @Schema(description = "Email користувача", example = "user@example.com")
    private String email;

    @Schema(description = "Пароль", example = "secret123")
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
