package com.sandevistan.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class SpeedAbility {
    public static final UUID SPEED_MODIFIER_UUID =
            UUID.fromString("6a02e604-5f5c-4e89-a2e6-cf76a81a7b1b");
    public static final String SPEED_MODIFIER_NAME = "Sandevistan Speed Boost";

    /**
     * Applies a transient movement speed modifier to the entity in Forge 1.20.1.
     * MULTIPLY_BASE operation adds multiplierBonus to base movement speed.
     */
    public static void applySpeedModifier(LivingEntity entity, double multiplierBonus) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            if (attr.getModifier(SPEED_MODIFIER_UUID) != null) {
                attr.removeModifier(SPEED_MODIFIER_UUID);
            }
            attr.addTransientModifier(new AttributeModifier(
                    SPEED_MODIFIER_UUID,
                    SPEED_MODIFIER_NAME,
                    multiplierBonus,
                    AttributeModifier.Operation.MULTIPLY_BASE
            ));
        }
    }

    /**
     * Removes the transient speed modifier from the entity.
     */
    public static void removeSpeedModifier(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.getModifier(SPEED_MODIFIER_UUID) != null) {
            attr.removeModifier(SPEED_MODIFIER_UUID);
        }
    }

    public static boolean hasSpeedModifier(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        return attr != null && attr.getModifier(SPEED_MODIFIER_UUID) != null;
    }
}
