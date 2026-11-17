package com.sandevistan.client;

import com.sandevistan.SandevistanMod;
import com.sandevistan.core.SandevistanState;
import com.sandevistan.network.ActivatePayload;
import com.sandevistan.network.SyncStatePayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SandevistanMod.MOD_ID, value = Dist.CLIENT)
public class SandevistanClient {
    private static final SandevistanState CLIENT_STATE = new SandevistanState();

    public static SandevistanState getClientState() {
        return CLIENT_STATE;
    }

    public static void handleStateSync(SyncStatePayload payload) {
        try {
            CLIENT_STATE.setState(SandevistanState.State.valueOf(payload.stateName()));
        } catch (IllegalArgumentException e) {
            CLIENT_STATE.setState(SandevistanState.State.READY);
        }
        CLIENT_STATE.setActiveTicksRemaining(payload.activeTicksRemaining());
        CLIENT_STATE.setTotalActiveTicks(payload.totalActiveTicks());
        CLIENT_STATE.setCooldownTicksRemaining(payload.cooldownTicksRemaining());
        CLIENT_STATE.setTotalCooldownTicks(payload.totalCooldownTicks());
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        while (ClientKeybindings.KEY_ACTIVATE.consumeClick()) {
            PacketDistributor.sendToServer(new ActivatePayload());
        }

        CLIENT_STATE.tick();
    }
}
