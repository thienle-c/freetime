package com.sandevistan.network;

import com.sandevistan.SandevistanMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncStatePayload(
        String stateName,
        int activeTicksRemaining,
        int totalActiveTicks,
        int cooldownTicksRemaining,
        int totalCooldownTicks
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncStatePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SandevistanMod.MOD_ID, "sync_state"));

    public static final StreamCodec<FriendlyByteBuf, SyncStatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SyncStatePayload::stateName,
            ByteBufCodecs.VAR_INT, SyncStatePayload::activeTicksRemaining,
            ByteBufCodecs.VAR_INT, SyncStatePayload::totalActiveTicks,
            ByteBufCodecs.VAR_INT, SyncStatePayload::cooldownTicksRemaining,
            ByteBufCodecs.VAR_INT, SyncStatePayload::totalCooldownTicks,
            SyncStatePayload::new
    );

    @Override
    public CustomPacketPayload.Type<SyncStatePayload> type() {
        return TYPE;
    }

    public static void handle(SyncStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientPayloadBridge.handleSyncState(payload);
        });
    }
}
