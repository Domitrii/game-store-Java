package com.example.gameplatform.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * US-3: Перегляд каталогу ігор
 * US-4: Перегляд детальної інформації про гру
 * US-5: Фільтрація ігор за жанром
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("US-3, US-4, US-5: Каталог ігор")
class GameIntegrationTest {

    @Autowired MockMvc mockMvc;

    // ─── US-3: Каталог ───────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-08 [+] Отримати всі ігри без авторизації → 200 + непорожній список")
    void getAllGames_noAuth_returns200WithList() throws Exception {
        mockMvc.perform(get("/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    @DisplayName("TC-09 [+] Каталог містить поля title, genre, price")
    void getAllGames_responseHasRequiredFields() throws Exception {
        mockMvc.perform(get("/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").isNotEmpty())
                .andExpect(jsonPath("$[0].genre").isNotEmpty())
                .andExpect(jsonPath("$[0].price").isNumber());
    }

    // ─── US-4: Гра за ID ─────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-10 [+] Отримати гру за існуючим ID → 200 + дані гри")
    void getGameById_existingId_returns200() throws Exception {
        mockMvc.perform(get("/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("The Witcher 3: Wild Hunt"))
                .andExpect(jsonPath("$.genre").value("RPG"))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    @Test
    @DisplayName("TC-11 [+] Отримати безкоштовну гру (price=0) → 200")
    void getGameById_freeGame_returns200WithZeroPrice() throws Exception {
        mockMvc.perform(get("/games/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Counter-Strike 2"))
                .andExpect(jsonPath("$.price").value(0.0));
    }

    @Test
    @DisplayName("TC-12 [-] Отримати гру з неіснуючим ID → 404")
    void getGameById_nonExistingId_returns404() throws Exception {
        mockMvc.perform(get("/games/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @DisplayName("TC-13 [-] Отримати гру з ID=0 → 404")
    void getGameById_zeroId_returns404() throws Exception {
        mockMvc.perform(get("/games/0"))
                .andExpect(status().isNotFound());
    }

    // ─── US-5: Фільтр за жанром ──────────────────────────────────────────────

    @Test
    @DisplayName("TC-14 [+] Фільтр за жанром RPG → 200 + лише RPG ігри")
    void getByGenre_validGenre_returnsMatchingGames() throws Exception {
        mockMvc.perform(get("/games/genre/RPG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].genre").value("RPG"));
    }

    @Test
    @DisplayName("TC-15 [+] Фільтр за жанром SHOOTER → 200")
    void getByGenre_shooter_returns200() throws Exception {
        mockMvc.perform(get("/games/genre/SHOOTER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genre").value("SHOOTER"));
    }

    @Test
    @DisplayName("TC-16 [+] Фільтр за жанром PUZZLE → 200 (Portal 2)")
    void getByGenre_puzzle_returnsPortal2() throws Exception {
        mockMvc.perform(get("/games/genre/PUZZLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Portal 2"));
    }

    @Test
    @DisplayName("TC-17 [-] Фільтр за неіснуючим жанром → 400")
    void getByGenre_invalidGenre_returns400() throws Exception {
        mockMvc.perform(get("/games/genre/INVALID_GENRE"))
                .andExpect(status().isBadRequest());
    }
}
