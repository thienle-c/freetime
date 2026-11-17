package com.sandevistan.dilation;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectileDilationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectileDilationHandler.class);

    /**
     * Applies dilation slowdown to a newly entered projectile.
     */
    public static void applyDilation(Projectile projectile, DilatedProjectileData data, double factor) {
        if (data.isDilated()) {
            return;
        }

        Vec3 velocity = projectile.getDeltaMovement();
        data.setPreDilationVelocity(velocity);

        double clampedFactor = Math.max(0.05, Math.min(0.95, factor));
        Vec3 dilatedVelocity = velocity.scale(clampedFactor);
        projectile.setDeltaMovement(dilatedVelocity);
        projectile.hasImpulse = true;
        data.setDilated(true);

        LOGGER.debug("Dilated projectile {} (velocity: {} -> {})",
                projectile.getUUID(), velocity, dilatedVelocity);
    }

    /**
     * Ticks a dilated projectile each game tick, applying arc gravity compensation.
     */
    public static void tickDilation(Projectile projectile, double factor) {
        if (!projectile.isAlive()) {
            return;
        }

        double clampedFactor = Math.max(0.05, Math.min(0.95, factor));

        // Compensate for vanilla gravity so slow-moving projectile doesn't drop prematurely
        double gravityCompensation = 0.0;
        if (projectile instanceof AbstractArrow) {
            // Standard arrow gravity in vanilla is 0.05 per tick
            gravityCompensation = 0.05 * (1.0 - clampedFactor);
        } else if (projectile instanceof ThrowableProjectile) {
            // Standard throwable gravity in vanilla is 0.03 per tick
            gravityCompensation = 0.03 * (1.0 - clampedFactor);
        }

        if (gravityCompensation > 0.0) {
            Vec3 current = projectile.getDeltaMovement();
            projectile.setDeltaMovement(current.x, current.y + gravityCompensation, current.z);
            projectile.hasImpulse = true;
        }
    }

    /**
     * Restores a projectile's original flight velocity when exiting the dilation field.
     */
    public static void restoreDilation(Projectile projectile, DilatedProjectileData data, double factor) {
        if (!data.isDilated() || !projectile.isAlive()) {
            return;
        }

        double clampedFactor = Math.max(0.05, Math.min(0.95, factor));
        Vec3 current = projectile.getDeltaMovement();
        Vec3 restored = current.scale(1.0 / clampedFactor);

        projectile.setDeltaMovement(restored);
        projectile.hasImpulse = true;
        data.setDilated(false);

        LOGGER.debug("Restored projectile {} velocity to {}", projectile.getUUID(), restored);
    }
}
