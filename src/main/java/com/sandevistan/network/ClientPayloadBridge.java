package com.sandevistan.network;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ClientPayloadBridge {
    public static void handleSyncState(SyncStatePacket packet) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientDelegate.syncState(packet);
        }
    }

    private static class ClientDelegate {
        private static void syncState(SyncStatePacket packet) {
            com.sandevistan.client.SandevistanClient.handleStateSync(packet);
        }
    }
}
