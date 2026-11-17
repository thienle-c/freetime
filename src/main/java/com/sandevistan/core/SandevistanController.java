package com.sandevistan.core;

import com.sandevistan.ability.SpeedAbility;
import com.sandevistan.config.ModConfig;
import com.sandevistan.network.SyncStatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = com.sandevistan.SandevistanMod.MOD_ID)
public class SandevistanController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SandevistanController.class);

    public static void handleActivationRequest(ServerPlayer player) {
        if (!player.isAlive() || player.isSpectator()) {
            return;
        }

        SandevistanState state = player.getData(ModAttachments.SANDEVISTAN_STATE);
        if (state.isReady()) {
            int duration = ModConfig.COMMON.activeDurationTicks.get();
            int cooldown = ModConfig.COMMON.cooldownTicks.get();
            double speedMultiplier = ModConfig.COMMON.speedMultiplier.get();

            state.activate(duration, cooldown);
            SpeedAbility.applySpeedModifier(player, speedMultiplier);
            syncToPlayer(player, state);

            LOGGER.debug("Player {} activated Sandevistan (duration: {} ticks, cooldown: {} ticks)",
                    player.getScoreboardName(), duration, cooldown);
        } else if (state.isActive()) {
            // Early deactivation by pressing key again
            state.deactivate();
            SpeedAbility.removeSpeedModifier(player);
            syncToPlayer(player, state);

            LOGGER.debug("Player {} manually deactivated Sandevistan", player.getScoreboardName());
        }
    }

    public static void syncToPlayer(ServerPlayer player, SandevistanState state) {
        PacketDistributor.sendToPlayer(
                player,
                new SyncStatePayload(
                        state.getState().name(),
                        state.getActiveTicksRemaining(),
                        state.getTotalActiveTicks(),
                        state.getCooldownTicksRemaining(),
                        state.getTotalCooldownTicks()
                )
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SandevistanState state = player.getData(ModAttachments.SANDEVISTAN_STATE);
            boolean transitioned = state.tick();

            if (transitioned) {
                if (state.isCooldown()) {
                    SpeedAbility.removeSpeedModifier(player);
                    syncToPlayer(player, state);
                    LOGGER.debug("Player {} Sandevistan expired -> entering COOLDOWN", player.getScoreboardName());
                } else if (state.isReady()) {
                    syncToPlayer(player, state);
                    LOGGER.debug("Player {} Sandevistan cooldown finished -> READY", player.getScoreboardName());
                }
            } else {
                if (state.isActive()) {
                    if (!SpeedAbility.hasSpeedModifier(player)) {
                        SpeedAbility.applySpeedModifier(player, ModConfig.COMMON.speedMultiplier.get());
                    }
                    if (state.getActiveTicksRemaining() % 5 == 0) {
                        syncToPlayer(player, state);
                    }
                } else if (state.isCooldown()) {
                    if (state.getCooldownTicksRemaining() % 20 == 0) {
                        syncToPlayer(player, state);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SpeedAbility.removeSpeedModifier(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SpeedAbility.removeSpeedModifier(player);
            SandevistanState state = player.getData(ModAttachments.SANDEVISTAN_STATE);
            state.reset();
            syncToPlayer(player, state);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SandevistanState state = player.getData(ModAttachments.SANDEVISTAN_STATE);
            if (state.isActive()) {
                SpeedAbility.applySpeedModifier(player, ModConfig.COMMON.speedMultiplier.get());
            } else {
                SpeedAbility.removeSpeedModifier(player);
            }
            syncToPlayer(player, state);
        }
    }
}
