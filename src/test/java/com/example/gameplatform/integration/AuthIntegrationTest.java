package com.example.gameplatform.integration;

import com.example.gameplatform.dto.LoginRequest;
import com.example.gameplatform.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * US-1: Реєстрація нового користувача
 * US-2: Вхід в систему та отримання JWT-токена
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("US-1 & US-2: Автентифікація")
class AuthIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    // ─── Допоміжний метод ────────────────────────────────────────────────────

    private String uniqueEmail() {
        return "user_" + UUID.randomUUID() + "@test.com";
    }

    private User buildUser(String email, String password) {
        User u = new User();
        u.setEmail(email);
        u.setName("Test User");
        u.setPasswordHash(password);
        return u;
    }

    private void registerUser(String email, String password) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser(email, password))))
                .andExpect(status().isCreated());
    }

    // ─── US-1: Реєстрація ────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-01 [+] Реєстрація з валідними даними → 201")
    void register_validData_returns201() throws Exception {
        String email = uniqueEmail();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser(email, "pass1234"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.passwordHash").value(org.hamcrest.Matchers.not("pass1234")));
    }

    @Test
    @DisplayName("TC-02 [-] Реєстрація з email що вже існує → 409")
    void register_duplicateEmail_returns409() throws Exception {
        String email = uniqueEmail();
        registerUser(email, "pass1234");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser(email, "otherPass"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("already registered")));
    }

    @Test
    @DisplayName("TC-03 [-] Реєстрація без email → 400")
    void register_missingEmail_returns4xx() throws Exception {
        User user = new User();
        user.setName("No Email");
        user.setPasswordHash("pass1234");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    // ─── US-2: Вхід ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-04 [+] Вхід з правильними даними → 200 + JWT токен")
    void login_validCredentials_returns200WithToken() throws Exception {
        String email = uniqueEmail();
        String password = "correctPass";
        registerUser(email, password);

        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword(password);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    @DisplayName("TC-05 [-] Вхід з неправильним паролем → 401")
    void login_wrongPassword_returns401() throws Exception {
        String email = uniqueEmail();
        registerUser(email, "correctPass");

        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword("wrongPass");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TC-06 [-] Вхід з неіснуючим email → 401")
    void login_unknownEmail_returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("nobody_" + UUID.randomUUID() + "@test.com");
        req.setPassword("anyPass");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TC-07 [-] Вхід з порожнім паролем → 401")
    void login_emptyPassword_returns401() throws Exception {
        String email = uniqueEmail();
        registerUser(email, "realPass");

        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword("");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
