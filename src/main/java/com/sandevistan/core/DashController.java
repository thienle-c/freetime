package com.sandevistan.core;

import com.sandevistan.ability.DashAbility;
import com.sandevistan.config.ModConfig;
import com.sandevistan.network.DashPacket;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashController.class);

    public static void handleDashRequest(ServerPlayer player, DashPacket packet) {
        if (!player.isAlive() || player.isSpectator()) {
            return;
        }

        player.getCapability(SandevistanCapability.CAPABILITY).ifPresent(state -> {
            if (!state.canDash()) {
                LOGGER.debug("Dash rejected for {}: Sandevistan not active or no charges (state: {}, charges: {})",
                        player.getScoreboardName(), state.getState(), state.getDashCharges());
                return;
            }

            boolean executed = DashAbility.executeDash(
                    player,
                    packet.getForward(),
                    packet.getStrafe(),
                    packet.isJumping(),
                    packet.getYaw()
            );

            if (executed) {
                int rechargeCooldown = ModConfig.COMMON.dashCooldownTicks.get();
                state.consumeDashCharge(rechargeCooldown);
                SandevistanController.syncToPlayer(player, state);

                LOGGER.debug("Dash executed for {}: remaining charges = {}/{}",
                        player.getScoreboardName(), state.getDashCharges(), state.getMaxDashCharges());
            }
        });
    }
}
