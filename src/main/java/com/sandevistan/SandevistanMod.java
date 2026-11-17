package com.sandevistan;

import com.sandevistan.config.ModConfig;
import com.sandevistan.core.ModAttachments;
import com.sandevistan.network.ModNetworking;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(SandevistanMod.MOD_ID)
public class SandevistanMod {
    public static final String MOD_ID = "sandevistan";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public SandevistanMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing Sandevistan Overdrive mod...");

        // Register configuration specifications
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ModConfig.COMMON_SPEC);
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, ModConfig.CLIENT_SPEC);

        // Register custom player data attachments
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);

        // Register network payloads on mod event bus
        modEventBus.addListener(ModNetworking::registerPayloads);

        // Client-specific initialization
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientInit.init(modEventBus);
        }

        LOGGER.info("Sandevistan Overdrive initialized successfully.");
    }

    private static class ClientInit {
        private static void init(IEventBus modEventBus) {
            modEventBus.addListener(com.sandevistan.client.ClientKeybindings::registerKeys);
            modEventBus.addListener(com.sandevistan.client.SandevistanHUD::registerGuiLayers);
        }
    }
}
