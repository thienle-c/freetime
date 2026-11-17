package com.sandevistan.network;

import com.sandevistan.SandevistanMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SandevistanMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;
    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        CHANNEL.messageBuilder(ActivatePacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(ActivatePacket::encode)
                .decoder(ActivatePacket::decode)
                .consumerMainThread(ActivatePacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncStatePacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncStatePacket::encode)
                .decoder(SyncStatePacket::decode)
                .consumerMainThread(SyncStatePacket::handle)
                .add();

        CHANNEL.messageBuilder(DashPacket.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(DashPacket::encode)
                .decoder(DashPacket::decode)
                .consumerMainThread(DashPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }

    public static void sendToPlayer(ServerPlayer player, Object msg) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
