package com.sandevistan.client;

import com.sandevistan.core.SandevistanState;
import com.sandevistan.network.ActivatePacket;
import com.sandevistan.network.ModNetworking;
import com.sandevistan.network.SyncStatePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SandevistanClient {
    private static final SandevistanState CLIENT_STATE = new SandevistanState();

    public static SandevistanState getClientState() {
        return CLIENT_STATE;
    }

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ClientKeybindings::registerKeys);
        modEventBus.addListener(SandevistanClient::registerOverlays);
        MinecraftForge.EVENT_BUS.register(SandevistanClient.class);
    }

    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "hud", SandevistanHUD.INSTANCE);
    }

    public static void handleStateSync(SyncStatePacket packet) {
        try {
            CLIENT_STATE.setState(SandevistanState.State.valueOf(packet.getStateName()));
        } catch (Exception e) {
            CLIENT_STATE.setState(SandevistanState.State.READY);
        }
        CLIENT_STATE.setActiveTicksRemaining(packet.getActiveTicksRemaining());
        CLIENT_STATE.setTotalActiveTicks(packet.getTotalActiveTicks());
        CLIENT_STATE.setCooldownTicksRemaining(packet.getCooldownTicksRemaining());
        CLIENT_STATE.setTotalCooldownTicks(packet.getTotalCooldownTicks());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        while (ClientKeybindings.KEY_ACTIVATE.consumeClick()) {
            ModNetworking.sendToServer(new ActivatePacket());
        }

        CLIENT_STATE.tick();
    }
}
