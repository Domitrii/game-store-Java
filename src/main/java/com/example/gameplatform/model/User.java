package com.example.gameplatform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id = UUID.randomUUID().toString();

    @Email(message = "Некоректний формат email")
    @NotBlank(message = "Email є обов'язковим")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Ім'я є обов'язковим")
    private String name;

    @NotBlank(message = "Пароль є обов'язковим")
    @Size(min = 6, message = "Пароль має містити мінімум 6 символів")
    private String passwordHash;

    private String avatarURL;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAvatarURL() { return avatarURL; }
    public void setAvatarURL(String avatarURL) { this.avatarURL = avatarURL; }
}
