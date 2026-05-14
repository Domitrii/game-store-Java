package com.example.gameplatform.integration;

import com.example.gameplatform.dto.LoginRequest;
import com.example.gameplatform.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * US-6: Придбання гри авторизованим користувачем
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("US-6: Покупка гри")
class PurchaseIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String token;

    // ─── Підготовка: реєструємо унікального юзера та отримуємо JWT ───────────

    @BeforeEach
    void setUp() throws Exception {
        String email = "buyer_" + UUID.randomUUID() + "@test.com";
        String password = "buyPass123";

        // Реєстрація
        User user = new User();
        user.setEmail(email);
        user.setName("Buyer");
        user.setPasswordHash(password);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        // Вхід → JWT
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail(email);
        loginReq.setPassword(password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        token = body.get("token").asText();
    }

    // ─── US-6: Покупка ───────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-18 [+] Покупка наявної гри з JWT → 201 + запис покупки")
    void purchase_validGameAndAuth_returns201() throws Exception {
        mockMvc.perform(post("/purchase")
                        .header("Authorization", "Bearer " + token)
                        .param("gameId", "3"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId").value(3))
                .andExpect(jsonPath("$.price").value(59.99))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("TC-19 [+] Покупка безкоштовної гри → 201, price=0")
    void purchase_freeGame_returns201WithZeroPrice() throws Exception {
        mockMvc.perform(post("/purchase")
                        .header("Authorization", "Bearer " + token)
                        .param("gameId", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(0.0))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("TC-20 [-] Покупка без JWT → 401")
    void purchase_noToken_returns401() throws Exception {
        mockMvc.perform(post("/purchase")
                        .param("gameId", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TC-21 [-] Покупка з невалідним JWT → 401")
    void purchase_invalidToken_returns401() throws Exception {
        mockMvc.perform(post("/purchase")
                        .header("Authorization", "Bearer invalid.token.here")
                        .param("gameId", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TC-22 [-] Повторна покупка однієї гри → 400")
    void purchase_alreadyPurchased_returns400() throws Exception {
        // Перша покупка
        mockMvc.perform(post("/purchase")
                        .header("Authorization", "Bearer " + token)
                        .param("gameId", "4"))
                .andExpect(status().isCreated());

        // Повторна покупка тієї самої гри
        mockMvc.perform(post("/purchase")
                        .header("Authorization", "Bearer " + token)
                        .param("gameId", "4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        org.hamcrest.Matchers.containsString("Already purchased")));
    }

    @Test
    @DisplayName("TC-23 [-] Покупка гри з неіснуючим ID → 404")
    void purchase_gameNotFound_returns404() throws Exception {
        mockMvc.perform(post("/purchase")
                        .header("Authorization", "Bearer " + token)
                        .param("gameId", "99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(
                        org.hamcrest.Matchers.containsString("not found")));
    }

    @Test
    @DisplayName("TC-24 [-] Покупка без параметра gameId → 400")
    void purchase_missingGameId_returns400() throws Exception {
        mockMvc.perform(post("/purchase")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }
}
