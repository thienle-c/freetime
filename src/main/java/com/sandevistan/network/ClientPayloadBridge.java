package com.sandevistan.network;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class ClientPayloadBridge {
    public static void handleSyncState(SyncStatePayload payload) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientDelegate.syncState(payload);
        }
    }

    private static class ClientDelegate {
        private static void syncState(SyncStatePayload payload) {
            com.sandevistan.client.SandevistanClient.handleStateSync(payload);
        }
    }
}
