package com.sandevistan.network;

import com.sandevistan.core.SandevistanController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ActivatePacket {

    public ActivatePacket() {
    }

    public static void encode(ActivatePacket msg, FriendlyByteBuf buf) {
    }

    public static ActivatePacket decode(FriendlyByteBuf buf) {
        return new ActivatePacket();
    }

    public static void handle(ActivatePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                SandevistanController.handleActivationRequest(player);
            }
        });
        ctx.setPacketHandled(true);
    }
}
