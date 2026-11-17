package com.sandevistan.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncStatePacket {
    private final String stateName;
    private final int activeTicksRemaining;
    private final int totalActiveTicks;
    private final int cooldownTicksRemaining;
    private final int totalCooldownTicks;

    public SyncStatePacket(String stateName, int activeTicksRemaining, int totalActiveTicks, int cooldownTicksRemaining, int totalCooldownTicks) {
        this.stateName = stateName;
        this.activeTicksRemaining = activeTicksRemaining;
        this.totalActiveTicks = totalActiveTicks;
        this.cooldownTicksRemaining = cooldownTicksRemaining;
        this.totalCooldownTicks = totalCooldownTicks;
    }

    public String getStateName() {
        return stateName;
    }

    public int getActiveTicksRemaining() {
        return activeTicksRemaining;
    }

    public int getTotalActiveTicks() {
        return totalActiveTicks;
    }

    public int getCooldownTicksRemaining() {
        return cooldownTicksRemaining;
    }

    public int getTotalCooldownTicks() {
        return totalCooldownTicks;
    }

    public static void encode(SyncStatePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.stateName);
        buf.writeVarInt(msg.activeTicksRemaining);
        buf.writeVarInt(msg.totalActiveTicks);
        buf.writeVarInt(msg.cooldownTicksRemaining);
        buf.writeVarInt(msg.totalCooldownTicks);
    }

    public static SyncStatePacket decode(FriendlyByteBuf buf) {
        return new SyncStatePacket(
                buf.readUtf(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt()
        );
    }

    public static void handle(SyncStatePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ClientPayloadBridge.handleSyncState(msg);
        });
        ctx.setPacketHandled(true);
    }
}
