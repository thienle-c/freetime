package com.sandevistan.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SandevistanState {

    public enum State {
        READY,
        ACTIVE,
        COOLDOWN
    }

    public static final Codec<SandevistanState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("state").forGetter(s -> s.getState().name()),
            Codec.INT.fieldOf("activeTicksRemaining").forGetter(SandevistanState::getActiveTicksRemaining),
            Codec.INT.fieldOf("totalActiveTicks").forGetter(SandevistanState::getTotalActiveTicks),
            Codec.INT.fieldOf("cooldownTicksRemaining").forGetter(SandevistanState::getCooldownTicksRemaining),
            Codec.INT.fieldOf("totalCooldownTicks").forGetter(SandevistanState::getTotalCooldownTicks)
    ).apply(instance, (stateName, activeRem, activeTot, cdRem, cdTot) -> {
        SandevistanState state = new SandevistanState();
        try {
            state.setState(State.valueOf(stateName));
        } catch (IllegalArgumentException e) {
            state.setState(State.READY);
        }
        state.setActiveTicksRemaining(activeRem);
        state.setTotalActiveTicks(activeTot);
        state.setCooldownTicksRemaining(cdRem);
        state.setTotalCooldownTicks(cdTot);
        return state;
    }));

    private State state = State.READY;
    private int activeTicksRemaining = 0;
    private int totalActiveTicks = 0;
    private int cooldownTicksRemaining = 0;
    private int totalCooldownTicks = 0;

    public SandevistanState() {
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isActive() {
        return state == State.ACTIVE;
    }

    public boolean isCooldown() {
        return state == State.COOLDOWN;
    }

    public boolean isReady() {
        return state == State.READY;
    }

    public int getActiveTicksRemaining() {
        return activeTicksRemaining;
    }

    public void setActiveTicksRemaining(int activeTicksRemaining) {
        this.activeTicksRemaining = activeTicksRemaining;
    }

    public int getTotalActiveTicks() {
        return totalActiveTicks;
    }

    public void setTotalActiveTicks(int totalActiveTicks) {
        this.totalActiveTicks = totalActiveTicks;
    }

    public int getCooldownTicksRemaining() {
        return cooldownTicksRemaining;
    }

    public void setCooldownTicksRemaining(int cooldownTicksRemaining) {
        this.cooldownTicksRemaining = cooldownTicksRemaining;
    }

    public int getTotalCooldownTicks() {
        return totalCooldownTicks;
    }

    public void setTotalCooldownTicks(int totalCooldownTicks) {
        this.totalCooldownTicks = totalCooldownTicks;
    }

    public boolean activate(int durationTicks, int cooldownTicks) {
        if (state != State.READY) {
            return false;
        }
        this.state = State.ACTIVE;
        this.totalActiveTicks = durationTicks;
        this.activeTicksRemaining = durationTicks;
        this.totalCooldownTicks = cooldownTicks;
        this.cooldownTicksRemaining = 0;
        return true;
    }

    public boolean deactivate() {
        if (state != State.ACTIVE) {
            return false;
        }
        this.state = State.COOLDOWN;
        this.activeTicksRemaining = 0;
        this.cooldownTicksRemaining = this.totalCooldownTicks;
        return true;
    }

    public void reset() {
        this.state = State.READY;
        this.activeTicksRemaining = 0;
        this.totalActiveTicks = 0;
        this.cooldownTicksRemaining = 0;
        this.totalCooldownTicks = 0;
    }

    /**
     * Advances timers by 1 tick on the server.
     * @return true if state transitioned (e.g. active expired, cooldown completed)
     */
    public boolean tick() {
        if (state == State.ACTIVE) {
            activeTicksRemaining--;
            if (activeTicksRemaining <= 0) {
                state = State.COOLDOWN;
                activeTicksRemaining = 0;
                cooldownTicksRemaining = totalCooldownTicks;
                return true;
            }
        } else if (state == State.COOLDOWN) {
            cooldownTicksRemaining--;
            if (cooldownTicksRemaining <= 0) {
                state = State.READY;
                cooldownTicksRemaining = 0;
                totalCooldownTicks = 0;
                return true;
            }
        }
        return false;
    }
}
