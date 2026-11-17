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

    // Phase 2: Dash State
    private int dashCharges = 0;
    private int maxDashCharges = 2;
    private int dashCooldownTicksRemaining = 0;
    private int totalDashCooldownTicks = 20;

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

    public int getDashCharges() {
        return dashCharges;
    }

    public void setDashCharges(int dashCharges) {
        this.dashCharges = dashCharges;
    }

    public int getMaxDashCharges() {
        return maxDashCharges;
    }

    public void setMaxDashCharges(int maxDashCharges) {
        this.maxDashCharges = maxDashCharges;
    }

    public int getDashCooldownTicksRemaining() {
        return dashCooldownTicksRemaining;
    }

    public void setDashCooldownTicksRemaining(int dashCooldownTicksRemaining) {
        this.dashCooldownTicksRemaining = dashCooldownTicksRemaining;
    }

    public int getTotalDashCooldownTicks() {
        return totalDashCooldownTicks;
    }

    public void setTotalDashCooldownTicks(int totalDashCooldownTicks) {
        this.totalDashCooldownTicks = totalDashCooldownTicks;
    }

    public boolean canDash() {
        return state == State.ACTIVE && dashCharges > 0;
    }

    public boolean consumeDashCharge(int rechargeTicks) {
        if (!canDash()) {
            return false;
        }
        this.dashCharges--;
        this.totalDashCooldownTicks = rechargeTicks > 0 ? rechargeTicks : 20;
        if (this.dashCooldownTicksRemaining <= 0) {
            this.dashCooldownTicksRemaining = this.totalDashCooldownTicks;
        }
        return true;
    }

    public boolean activate(int durationTicks, int cooldownTicks) {
        return activate(durationTicks, cooldownTicks, 2, 20);
    }

    public boolean activate(int durationTicks, int cooldownTicks, int maxCharges, int dashCooldown) {
        if (state != State.READY) {
            return false;
        }
        this.state = State.ACTIVE;
        this.totalActiveTicks = durationTicks;
        this.activeTicksRemaining = durationTicks;
        this.totalCooldownTicks = cooldownTicks;
        this.cooldownTicksRemaining = 0;
        this.maxDashCharges = Math.max(1, maxCharges);
        this.dashCharges = this.maxDashCharges;
        this.totalDashCooldownTicks = Math.max(1, dashCooldown);
        this.dashCooldownTicksRemaining = 0;
        return true;
    }

    public boolean deactivate() {
        if (state != State.ACTIVE) {
            return false;
        }
        this.state = State.COOLDOWN;
        this.activeTicksRemaining = 0;
        this.cooldownTicksRemaining = this.totalCooldownTicks;
        this.dashCharges = 0;
        this.dashCooldownTicksRemaining = 0;
        return true;
    }

    public void reset() {
        this.state = State.READY;
        this.activeTicksRemaining = 0;
        this.totalActiveTicks = 0;
        this.cooldownTicksRemaining = 0;
        this.totalCooldownTicks = 0;
        this.dashCharges = 0;
        this.dashCooldownTicksRemaining = 0;
    }

    public void copyFrom(SandevistanState other) {
        this.state = other.state;
        this.activeTicksRemaining = other.activeTicksRemaining;
        this.totalActiveTicks = other.totalActiveTicks;
        this.cooldownTicksRemaining = other.cooldownTicksRemaining;
        this.totalCooldownTicks = other.totalCooldownTicks;
        this.dashCharges = other.dashCharges;
        this.maxDashCharges = other.maxDashCharges;
        this.dashCooldownTicksRemaining = other.dashCooldownTicksRemaining;
        this.totalDashCooldownTicks = other.totalDashCooldownTicks;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("state", state.name());
        tag.putInt("activeTicksRemaining", activeTicksRemaining);
        tag.putInt("totalActiveTicks", totalActiveTicks);
        tag.putInt("cooldownTicksRemaining", cooldownTicksRemaining);
        tag.putInt("totalCooldownTicks", totalCooldownTicks);
        tag.putInt("dashCharges", dashCharges);
        tag.putInt("maxDashCharges", maxDashCharges);
        tag.putInt("dashCooldownTicksRemaining", dashCooldownTicksRemaining);
        tag.putInt("totalDashCooldownTicks", totalDashCooldownTicks);
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
        this.dashCharges = tag.getInt("dashCharges");
        this.maxDashCharges = tag.contains("maxDashCharges") ? tag.getInt("maxDashCharges") : 2;
        this.dashCooldownTicksRemaining = tag.getInt("dashCooldownTicksRemaining");
        this.totalDashCooldownTicks = tag.contains("totalDashCooldownTicks") ? tag.getInt("totalDashCooldownTicks") : 20;
    }

    /**
     * Advances timers by 1 tick on the server.
     * @return true if state transitioned (e.g. active expired, cooldown completed)
     */
    public boolean tick() {
        if (state == State.ACTIVE) {
            activeTicksRemaining--;

            // Tick dash recharge
            if (dashCharges < maxDashCharges) {
                dashCooldownTicksRemaining--;
                if (dashCooldownTicksRemaining <= 0) {
                    dashCharges++;
                    if (dashCharges < maxDashCharges) {
                        dashCooldownTicksRemaining = totalDashCooldownTicks > 0 ? totalDashCooldownTicks : 20;
                    } else {
                        dashCooldownTicksRemaining = 0;
                    }
                }
            }

            if (activeTicksRemaining <= 0) {
                state = State.COOLDOWN;
                activeTicksRemaining = 0;
                cooldownTicksRemaining = totalCooldownTicks;
                dashCharges = 0;
                dashCooldownTicksRemaining = 0;
                return true;
            }
        } else if (state == State.COOLDOWN) {
            dashCharges = 0;
            dashCooldownTicksRemaining = 0;
            cooldownTicksRemaining--;
            if (cooldownTicksRemaining <= 0) {
                state = State.READY;
                cooldownTicksRemaining = 0;
                totalCooldownTicks = 0;
                return true;
            }
        } else {
            dashCharges = 0;
            dashCooldownTicksRemaining = 0;
        }
        return false;
    }
}
