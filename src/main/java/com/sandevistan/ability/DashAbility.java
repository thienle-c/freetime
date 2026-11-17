package com.sandevistan.ability;

import com.sandevistan.config.ModConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashAbility {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashAbility.class);

    /**
     * Executes a directional dash for the server player with swept collision safety.
     *
     * @param player  The server player executing the dash
     * @param forward Forward input component (-1.0 to 1.0)
     * @param strafe  Strafe input component (-1.0 to 1.0, positive = left)
     * @param jumping Whether jump key is held
     * @param yaw     The player's yaw orientation
     * @return true if dash was executed, false if rejected or blocked
     */
    public static boolean executeDash(ServerPlayer player, float forward, float strafe, boolean jumping, float yaw) {
        if (!player.isAlive() || player.isSpectator()) {
            return false;
        }

        if (!ModConfig.COMMON.dashEnabled.get()) {
            return false;
        }

        boolean inAir = !player.onGround();
        if (inAir && !ModConfig.COMMON.dashAirEnabled.get()) {
            return false;
        }

        // Calculate normalized 2D direction vector
        double[] dir = calculateDirection(forward, strafe, yaw);
        double dx = dir[0];
        double dz = dir[1];

        double baseSpeed = ModConfig.COMMON.dashSpeed.get();
        double targetSpeed = Math.max(0.2, Math.min(5.0, baseSpeed));

        // Determine vertical impulse
        double dy;
        if (inAir) {
            if (jumping) {
                dy = 0.25;
            } else {
                dy = Math.max(-0.05, player.getDeltaMovement().y * 0.5);
            }
        } else {
            if (jumping) {
                dy = 0.35;
            } else {
                dy = 0.05; // slight pop to step over small bumps
            }
        }

        ServerLevel level = player.serverLevel();
        AABB playerBox = player.getBoundingBox();

        // Check world border
        AABB destinationBox = playerBox.move(dx * targetSpeed, dy, dz * targetSpeed);
        if (!level.getWorldBorder().isWithinBounds(destinationBox)) {
            LOGGER.debug("Dash rejected for {}: outside world border", player.getScoreboardName());
            return false;
        }

        // Swept collision safety check: test substeps to prevent clipping through solid blocks
        double safeFactor = 1.0;
        int substeps = 4;
        for (int step = 1; step <= substeps; step++) {
            double factor = (double) step / substeps;
            AABB stepBox = playerBox.move(dx * targetSpeed * factor, dy * factor, dz * targetSpeed * factor);
            if (!level.noCollision(player, stepBox)) {
                // Collided at this step, back up to previous safe factor
                safeFactor = Math.max(0.2, (double) (step - 1) / substeps);
                break;
            }
        }

        double finalX = dx * targetSpeed * safeFactor;
        double finalY = dy;
        double finalZ = dz * targetSpeed * safeFactor;

        // Reset fall distance on dash start (prevents accumulated lethal fall before dash)
        player.fallDistance = 0.0f;

        // Apply server velocity impulse and mark for client sync
        player.setDeltaMovement(new Vec3(finalX, finalY, finalZ));
        player.hurtMarked = true;

        // Play feedback sounds and spawn visual particle trail
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS,
                0.8f,
                1.8f
        );

        level.sendParticles(
                ParticleTypes.ELECTRIC_SPARK,
                player.getX(),
                player.getY() + 0.5,
                player.getZ(),
                12,
                0.3,
                0.3,
                0.3,
                0.05
        );

        LOGGER.debug("Player {} dashed (dx: {}, dz: {}, safeFactor: {})",
                player.getScoreboardName(), finalX, finalZ, safeFactor);

        return true;
    }

    public static double[] calculateDirection(float forward, float strafe, float yaw) {
        float f = sanitizeFloat(forward, -1.0f, 1.0f);
        float s = sanitizeFloat(strafe, -1.0f, 1.0f);
        float playerYaw = sanitizeFloat(yaw, -3600.0f, 3600.0f);

        double yawRad = Math.toRadians(playerYaw);
        double sin = Math.sin(yawRad);
        double cos = Math.cos(yawRad);

        double dx;
        double dz;

        if (Math.abs(f) < 0.001f && Math.abs(s) < 0.001f) {
            dx = -sin;
            dz = cos;
        } else {
            double inputLen = Math.sqrt(f * f + s * s);
            double normF = f / inputLen;
            double normS = s / inputLen;

            dx = -sin * normF + cos * normS;
            dz = cos * normF + sin * normS;
        }

        double dirLen = Math.sqrt(dx * dx + dz * dz);
        if (dirLen < 0.0001) {
            dx = -sin;
            dz = cos;
        } else {
            dx /= dirLen;
            dz /= dirLen;
        }

        return new double[]{dx, dz};
    }

    private static float sanitizeFloat(float val, float min, float max) {
        if (Float.isNaN(val) || Float.isInfinite(val)) {
            return 0.0f;
        }
        return Math.max(min, Math.min(max, val));
    }
}
