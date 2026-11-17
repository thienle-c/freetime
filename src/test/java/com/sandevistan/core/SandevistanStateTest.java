package com.sandevistan.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SandevistanStateTest {

    private SandevistanState state;

    @BeforeEach
    void setUp() {
        state = new SandevistanState();
    }

    @Test
    @DisplayName("Initial state must be READY with zero timers")
    void testInitialStateIsReady() {
        assertEquals(SandevistanState.State.READY, state.getState());
        assertTrue(state.isReady());
        assertFalse(state.isActive());
        assertFalse(state.isCooldown());
        assertEquals(0, state.getActiveTicksRemaining());
        assertEquals(0, state.getCooldownTicksRemaining());
    }

    @Test
    @DisplayName("Activation sets ACTIVE state and initializes timers")
    void testActivationTransitionsToActive() {
        boolean activated = state.activate(160, 400);

        assertTrue(activated);
        assertEquals(SandevistanState.State.ACTIVE, state.getState());
        assertTrue(state.isActive());
        assertFalse(state.isReady());
        assertEquals(160, state.getActiveTicksRemaining());
        assertEquals(160, state.getTotalActiveTicks());
        assertEquals(400, state.getTotalCooldownTicks());
    }

    @Test
    @DisplayName("Cannot activate when already ACTIVE or in COOLDOWN")
    void testCannotActivateWhenNotReady() {
        state.activate(160, 400);
        assertFalse(state.activate(160, 400));

        state.deactivate();
        assertTrue(state.isCooldown());
        assertFalse(state.activate(160, 400));
    }

    @Test
    @DisplayName("Active state counts down and automatically enters COOLDOWN upon expiration")
    void testActiveCountdownAndExpiration() {
        state.activate(3, 10);

        assertFalse(state.tick()); // tick 1: 2 remaining
        assertEquals(2, state.getActiveTicksRemaining());
        assertTrue(state.isActive());

        assertFalse(state.tick()); // tick 2: 1 remaining
        assertEquals(1, state.getActiveTicksRemaining());
        assertTrue(state.isActive());

        boolean transitioned = state.tick(); // tick 3: expires -> enters COOLDOWN
        assertTrue(transitioned);
        assertEquals(SandevistanState.State.COOLDOWN, state.getState());
        assertTrue(state.isCooldown());
        assertEquals(0, state.getActiveTicksRemaining());
        assertEquals(10, state.getCooldownTicksRemaining());
    }

    @Test
    @DisplayName("Cooldown counts down and automatically returns to READY")
    void testCooldownCountdownAndCompletion() {
        state.activate(1, 2);
        state.tick(); // transitions to COOLDOWN with 2 ticks

        assertTrue(state.isCooldown());
        assertEquals(2, state.getCooldownTicksRemaining());

        assertFalse(state.tick()); // tick 1: 1 remaining
        assertTrue(state.isCooldown());
        assertEquals(1, state.getCooldownTicksRemaining());

        boolean transitioned = state.tick(); // tick 2: finished -> enters READY
        assertTrue(transitioned);
        assertEquals(SandevistanState.State.READY, state.getState());
        assertTrue(state.isReady());
        assertEquals(0, state.getCooldownTicksRemaining());
    }

    @Test
    @DisplayName("Manual deactivation while ACTIVE immediately enters COOLDOWN with full cooldown duration")
    void testManualDeactivation() {
        state.activate(160, 400);
        state.tick();
        state.tick();

        boolean deactivated = state.deactivate();
        assertTrue(deactivated);
        assertEquals(SandevistanState.State.COOLDOWN, state.getState());
        assertEquals(0, state.getActiveTicksRemaining());
        assertEquals(400, state.getCooldownTicksRemaining());
    }

    @Test
    @DisplayName("Reset resets state back to READY cleanly")
    void testReset() {
        state.activate(160, 400);
        state.reset();

        assertEquals(SandevistanState.State.READY, state.getState());
        assertEquals(0, state.getActiveTicksRemaining());
        assertEquals(0, state.getTotalActiveTicks());
        assertEquals(0, state.getCooldownTicksRemaining());
        assertEquals(0, state.getTotalCooldownTicks());
    }
}
