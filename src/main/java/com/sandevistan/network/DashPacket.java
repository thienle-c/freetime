package com.sandevistan.network;

import com.sandevistan.core.DashController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DashPacket {
    private final float forward;
    private final float strafe;
    private final boolean jumping;
    private final float yaw;

    public DashPacket(float forward, float strafe, boolean jumping, float yaw) {
        this.forward = forward;
        this.strafe = strafe;
        this.jumping = jumping;
        this.yaw = yaw;
    }

    public float getForward() {
        return forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public boolean isJumping() {
        return jumping;
    }

    public float getYaw() {
        return yaw;
    }

    public static void encode(DashPacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.forward);
        buf.writeFloat(msg.strafe);
        buf.writeBoolean(msg.jumping);
        buf.writeFloat(msg.yaw);
    }

    public static DashPacket decode(FriendlyByteBuf buf) {
        return new DashPacket(
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readFloat()
        );
    }

    public static void handle(DashPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender != null) {
                DashController.handleDashRequest(sender, msg);
            }
        });
        ctx.setPacketHandled(true);
    }
}
