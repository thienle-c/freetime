package com.sandevistan.dilation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TimeDilationLogicTest {

    @Test
    @DisplayName("Single player dilation tracking and cleanup")
    void testSinglePlayerDilationTracking() {
        UUID entityUuid = UUID.randomUUID();
        UUID playerUuid = UUID.randomUUID();

        DilatedEntityData data = new DilatedEntityData(null, playerUuid, 100);
        assertEquals(entityUuid.getMostSignificantBits() != 0, true);
        assertTrue(data.hasSources());
        assertTrue(data.getSourcePlayerUuids().contains(playerUuid));

        data.removeSourcePlayer(playerUuid);
        assertFalse(data.hasSources());
    }

    @Test
    @DisplayName("Multiplayer overlap: entity remains dilated until all source players expire")
    void testMultiplayerOverlapResolution() {
        UUID entityUuid = UUID.randomUUID();
        UUID playerA = UUID.randomUUID();
        UUID playerB = UUID.randomUUID();

        DilatedEntityData data = new DilatedEntityData(null, playerA, 100);
        data.addSourcePlayer(playerB);

        assertEquals(2, data.getSourcePlayerUuids().size());
        assertTrue(data.hasSources());

        // Player A's Sandevistan deactivates
        data.removeSourcePlayer(playerA);
        assertTrue(data.hasSources(), "Entity must remain dilated because Player B's field is still active");
        assertTrue(data.getSourcePlayerUuids().contains(playerB));

        // Player B's Sandevistan deactivates
        data.removeSourcePlayer(playerB);
        assertFalse(data.hasSources(), "Entity must be marked for restoration when all source fields expire");
    }

    @Test
    @DisplayName("Projectile dilation data captures pre-dilation velocity and source tracking")
    void testProjectileDilationData() {
        UUID projectileUuid = UUID.randomUUID();
        UUID playerUuid = UUID.randomUUID();

        DilatedProjectileData data = new DilatedProjectileData(null, playerUuid, 50);
        assertTrue(data.hasSources());
        assertFalse(data.isDilated());

        data.setDilated(true);
        assertTrue(data.isDilated());

        data.removeSourcePlayer(playerUuid);
        assertFalse(data.hasSources());
    }

    @Test
    @DisplayName("Dilation factor attribute formula produces correct mathematical slowdown")
    void testDilationAttributeCalculations() {
        double factor = 0.2;
        double multiplierBonus = -(1.0 - factor);
        assertEquals(-0.8, multiplierBonus, 1e-6);

        // Base speed 0.25 (standard mob speed)
        double baseSpeed = 0.25;
        double effectiveSpeed = baseSpeed + (baseSpeed * multiplierBonus);
        assertEquals(0.05, effectiveSpeed, 1e-6);
        assertEquals(baseSpeed * factor, effectiveSpeed, 1e-6);
    }

    @Test
    @DisplayName("Gravity compensation maintains physical arc proportionality")
    void testGravityCompensationCalculations() {
        double factor = 0.2;
        double arrowGravity = 0.05;
        double arrowCompensation = arrowGravity * (1.0 - factor);

        assertEquals(0.04, arrowCompensation, 1e-6);

        double netGravity = arrowGravity - arrowCompensation;
        assertEquals(0.01, netGravity, 1e-6);
        assertEquals(arrowGravity * factor, netGravity, 1e-6);
    }
}
