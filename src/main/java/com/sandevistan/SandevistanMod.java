package com.sandevistan;

import com.sandevistan.client.SandevistanClient;
import com.sandevistan.config.ModConfig;
import com.sandevistan.core.SandevistanCapability;
import com.sandevistan.core.SandevistanController;
import com.sandevistan.network.ModNetworking;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(SandevistanMod.MOD_ID)
public class SandevistanMod {
    public static final String MOD_ID = "sandevistan";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public SandevistanMod() {
        LOGGER.info("Initializing Sandevistan Overdrive for Minecraft Forge 1.20.1...");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register configuration specifications
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, ModConfig.CLIENT_SPEC);

        // Register capabilities
        modEventBus.addListener(SandevistanCapability::register);

        // Register networking
        ModNetworking.register();

        // Register game event listeners
        MinecraftForge.EVENT_BUS.register(SandevistanController.class);
        MinecraftForge.EVENT_BUS.register(SandevistanCapability.class);
        MinecraftForge.EVENT_BUS.register(com.sandevistan.dilation.TimeDilationController.class);

        // Client initialization
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SandevistanClient.init(modEventBus));

        LOGGER.info("Sandevistan Overdrive (Forge 1.20.1) initialized successfully.");
    }
}
