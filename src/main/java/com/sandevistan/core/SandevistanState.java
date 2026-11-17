package com.sandevistan.core;

import net.minecraft.nbt.CompoundTag;

public class SandevistanState {

    public enum State {
        READY,
        ACTIVE,
        COOLDOWN
    }

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

    public void copyFrom(SandevistanState other) {
        this.state = other.state;
        this.activeTicksRemaining = other.activeTicksRemaining;
        this.totalActiveTicks = other.totalActiveTicks;
        this.cooldownTicksRemaining = other.cooldownTicksRemaining;
        this.totalCooldownTicks = other.totalCooldownTicks;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("state", state.name());
        tag.putInt("activeTicksRemaining", activeTicksRemaining);
        tag.putInt("totalActiveTicks", totalActiveTicks);
        tag.putInt("cooldownTicksRemaining", cooldownTicksRemaining);
        tag.putInt("totalCooldownTicks", totalCooldownTicks);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        try {
            this.state = State.valueOf(tag.getString("state"));
        } catch (Exception e) {
            this.state = State.READY;
        }
        this.activeTicksRemaining = tag.getInt("activeTicksRemaining");
        this.totalActiveTicks = tag.getInt("totalActiveTicks");
        this.cooldownTicksRemaining = tag.getInt("cooldownTicksRemaining");
        this.totalCooldownTicks = tag.getInt("totalCooldownTicks");
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
