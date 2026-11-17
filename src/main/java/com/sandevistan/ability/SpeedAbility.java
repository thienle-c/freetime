package com.sandevistan.ability;

import com.sandevistan.SandevistanMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SpeedAbility {
    public static final ResourceLocation SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(SandevistanMod.MOD_ID, "speed_boost");

    /**
     * Applies a transient movement speed modifier to the entity.
     * Transient modifiers are not saved to disk and disappear cleanly on death/logout if not already removed.
     */
    public static void applySpeedModifier(LivingEntity entity, double multiplierBonus) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            if (attr.hasModifier(SPEED_MODIFIER_ID)) {
                attr.removeModifier(SPEED_MODIFIER_ID);
            }
            attr.addTransientModifier(new AttributeModifier(
                    SPEED_MODIFIER_ID,
                    multiplierBonus,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }
    }

    /**
     * Removes the transient speed modifier from the entity.
     */
    public static void removeSpeedModifier(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.hasModifier(SPEED_MODIFIER_ID)) {
            attr.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    public static boolean hasSpeedModifier(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        return attr != null && attr.hasModifier(SPEED_MODIFIER_ID);
    }
}
