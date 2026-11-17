package com.sandevistan.ability;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DashAbilityTest {

    @Test
    @DisplayName("Forward dash at yaw 0 produces unit vector pointing South (+Z)")
    void testForwardDash() {
        double[] dir = DashAbility.calculateDirection(1.0f, 0.0f, 0.0f);
        assertEquals(0.0, dir[0], 1e-4);
        assertEquals(1.0, dir[1], 1e-4);

        double len = Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1]);
        assertEquals(1.0, len, 1e-4);
    }

    @Test
    @DisplayName("Backward dash at yaw 0 produces unit vector pointing North (-Z)")
    void testBackwardDash() {
        double[] dir = DashAbility.calculateDirection(-1.0f, 0.0f, 0.0f);
        assertEquals(0.0, dir[0], 1e-4);
        assertEquals(-1.0, dir[1], 1e-4);

        double len = Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1]);
        assertEquals(1.0, len, 1e-4);
    }

    @Test
    @DisplayName("Left strafe dash at yaw 0 produces unit vector pointing East (+X)")
    void testLeftStrafeDash() {
        double[] dir = DashAbility.calculateDirection(0.0f, 1.0f, 0.0f);
        assertEquals(1.0, dir[0], 1e-4);
        assertEquals(0.0, dir[1], 1e-4);

        double len = Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1]);
        assertEquals(1.0, len, 1e-4);
    }

    @Test
    @DisplayName("Right strafe dash at yaw 0 produces unit vector pointing West (-X)")
    void testRightStrafeDash() {
        double[] dir = DashAbility.calculateDirection(0.0f, -1.0f, 0.0f);
        assertEquals(-1.0, dir[0], 1e-4);
        assertEquals(0.0, dir[1], 1e-4);

        double len = Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1]);
        assertEquals(1.0, len, 1e-4);
    }

    @Test
    @DisplayName("Diagonal dash is strictly normalized and does not exceed unit speed")
    void testDiagonalDashNormalized() {
        double[] dir = DashAbility.calculateDirection(1.0f, 1.0f, 0.0f);
        double len = Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1]);
        assertEquals(1.0, len, 1e-4, "Diagonal dash vector must have length exactly 1.0");

        double expected = 1.0 / Math.sqrt(2.0);
        assertEquals(expected, dir[0], 1e-4);
        assertEquals(expected, dir[1], 1e-4);
    }

    @Test
    @DisplayName("Forward dash when facing West (yaw 90) points correctly in -X")
    void testFacingWestDash() {
        double[] dir = DashAbility.calculateDirection(1.0f, 0.0f, 90.0f);
        assertEquals(-1.0, dir[0], 1e-4);
        assertEquals(0.0, dir[1], 1e-4);
    }

    @Test
    @DisplayName("Input sanitization handles NaN and Infinite values safely")
    void testSanitizationHandlesNaNAndInfinity() {
        assertDoesNotThrow(() -> {
            double[] dir = DashAbility.calculateDirection(Float.NaN, Float.POSITIVE_INFINITY, Float.NaN);
            assertNotNull(dir);
            assertFalse(Double.isNaN(dir[0]));
            assertFalse(Double.isNaN(dir[1]));
            assertFalse(Double.isInfinite(dir[0]));
            assertFalse(Double.isInfinite(dir[1]));
        });
    }
}
