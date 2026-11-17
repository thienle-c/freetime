package com.sandevistan.network;

import com.sandevistan.SandevistanMod;
import com.sandevistan.core.SandevistanController;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ActivatePayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ActivatePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SandevistanMod.MOD_ID, "activate"));

    public static final StreamCodec<ByteBuf, ActivatePayload> STREAM_CODEC =
            StreamCodec.unit(new ActivatePayload());

    @Override
    public CustomPacketPayload.Type<ActivatePayload> type() {
        return TYPE;
    }

    public static void handle(ActivatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                SandevistanController.handleActivationRequest(player);
            }
        });
    }
}
