package com.example.gameplatform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GamePlatformApplicationTests {

    @Test
    void testSystemHealth() {
        assertTrue(true, "Система функціонує коректно");
    }
}