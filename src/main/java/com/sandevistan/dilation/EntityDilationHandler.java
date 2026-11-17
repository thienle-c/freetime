package com.sandevistan.dilation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class EntityDilationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDilationHandler.class);

    public static final UUID TIME_DILATION_UUID =
            UUID.fromString("e7428760-4c74-4b55-a084-3be88b49e29a");
    public static final String TIME_DILATION_NAME = "Sandevistan Entity Dilation";

    /**
     * Applies the movement speed slowdown modifier to a living entity.
     *
     * @param entity         Target living entity
     * @param dilationFactor Time scale factor (e.g. 0.2 means 5x slower)
     */
    public static void applyDilation(LivingEntity entity, double dilationFactor) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            if (attr.getModifier(TIME_DILATION_UUID) != null) {
                attr.removeModifier(TIME_DILATION_UUID);
            }
            // Value is negative: e.g. for factor 0.2, multiplierBonus = -(1.0 - 0.2) = -0.8
            double multiplierBonus = -(1.0 - Math.max(0.05, Math.min(0.95, dilationFactor)));
            attr.addTransientModifier(new AttributeModifier(
                    TIME_DILATION_UUID,
                    TIME_DILATION_NAME,
                    multiplierBonus,
                    AttributeModifier.Operation.MULTIPLY_BASE
            ));
        }

        // Dampen current delta movement proportionately
        double clampedFactor = Math.max(0.05, Math.min(0.95, dilationFactor));
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(clampedFactor, 1.0, clampedFactor));
    }

    /**
     * Removes time dilation slowdown modifier from the living entity, restoring original speed.
     *
     * @param entity Target living entity
     */
    public static void removeDilation(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.getModifier(TIME_DILATION_UUID) != null) {
            attr.removeModifier(TIME_DILATION_UUID);
            LOGGER.debug("Restored original speed for entity {}", entity.getUUID());
        }
    }

    public static boolean hasDilation(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        return attr != null && attr.getModifier(TIME_DILATION_UUID) != null;
    }
}
