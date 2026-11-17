package com.sandevistan.network;

import com.sandevistan.SandevistanMod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(SandevistanMod.MOD_ID).versioned("1.0.0");

        registrar.playToServer(
                ActivatePayload.TYPE,
                ActivatePayload.STREAM_CODEC,
                ActivatePayload::handle
        );

        registrar.playToClient(
                SyncStatePayload.TYPE,
                SyncStatePayload.STREAM_CODEC,
                SyncStatePayload::handle
        );
    }
}
