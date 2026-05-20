package com.example.gameplatform.integration;

import com.example.gameplatform.dto.LoginRequest;
import com.example.gameplatform.model.Game;
import com.example.gameplatform.model.GameGenre;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * US-3 (CRUD): Повне управління каталогом ігор — Create, Update, Delete
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("US-3 CRUD: Управління іграми")
class GameCrudIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        String email = "admin_" + UUID.randomUUID() + "@test.com";
        User user = new User();
        user.setEmail(email);
        user.setName("Admin");
        user.setPasswordHash("adminPass123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword("adminPass123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn();

        token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    private Game buildGame(String title) {
        Game g = new Game();
        g.setTitle(title);
        g.setGenre(GameGenre.SHOOTER);
        g.setPlatform("PC");
        g.setPrice(29.99);
        g.setPublisher("Test Publisher");
        return g;
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-25 [+] Додати нову гру з JWT → 201")
    void createGame_validData_returns201() throws Exception {
        mockMvc.perform(post("/games")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildGame("New Test Game"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("New Test Game"))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    @Test
    @DisplayName("TC-26 [-] Додати гру без JWT → 401")
    void createGame_noAuth_returns401() throws Exception {
        mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildGame("Unauthorized Game"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TC-27 [-] Додати гру без назви → 400 (валідація)")
    void createGame_missingTitle_returns400() throws Exception {
        Game invalid = new Game();
        invalid.setGenre(GameGenre.RPG);
        invalid.setPlatform("PC");
        invalid.setPrice(9.99);

        mockMvc.perform(post("/games")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @DisplayName("TC-28 [-] Додати гру з від'ємною ціною → 400 (валідація)")
    void createGame_negativePrice_returns400() throws Exception {
        Game invalid = buildGame("Negative Price Game");
        invalid.setPrice(-5.0);

        mockMvc.perform(post("/games")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-29 [+] Оновити існуючу гру → 200 + нові дані")
    void updateGame_existingId_returns200() throws Exception {
        // Створюємо гру
        MvcResult created = mockMvc.perform(post("/games")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildGame("Original Title"))))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString())
                .get("id").asLong();

        // Оновлюємо
        Game updated = buildGame("Updated Title");
        updated.setPrice(49.99);

        mockMvc.perform(put("/games/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.price").value(49.99));
    }

    @Test
    @DisplayName("TC-30 [-] Оновити неіснуючу гру → 404")
    void updateGame_nonExistingId_returns404() throws Exception {
        mockMvc.perform(put("/games/99999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildGame("Ghost Game"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("TC-31 [-] Оновити гру без JWT → 401")
    void updateGame_noAuth_returns401() throws Exception {
        mockMvc.perform(put("/games/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildGame("Hack"))))
                .andExpect(status().isUnauthorized());
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-32 [+] Видалити існуючу гру → 204, після цього 404")
    void deleteGame_existingId_returns204() throws Exception {
        // Створюємо гру для видалення
        MvcResult created = mockMvc.perform(post("/games")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildGame("To Be Deleted"))))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString())
                .get("id").asLong();

        // Видаляємо
        mockMvc.perform(delete("/games/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Перевіряємо що гри більше немає
        mockMvc.perform(get("/games/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("TC-33 [-] Видалити неіснуючу гру → 404")
    void deleteGame_nonExistingId_returns404() throws Exception {
        mockMvc.perform(delete("/games/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("TC-34 [-] Видалити гру без JWT → 401")
    void deleteGame_noAuth_returns401() throws Exception {
        mockMvc.perform(delete("/games/1"))
                .andExpect(status().isUnauthorized());
    }
}
