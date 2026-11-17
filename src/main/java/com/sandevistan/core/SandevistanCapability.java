package com.sandevistan.core;

import com.sandevistan.SandevistanMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SandevistanMod.MOD_ID)
public class SandevistanCapability {
    public static final Capability<SandevistanState> CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static final ResourceLocation IDENTIFIER =
            new ResourceLocation(SandevistanMod.MOD_ID, "sandevistan_state");

    public static LazyOptional<SandevistanState> get(Player player) {
        return player.getCapability(CAPABILITY);
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final SandevistanState state = new SandevistanState();
        private final LazyOptional<SandevistanState> optional = LazyOptional.of(() -> state);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return state.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            state.deserializeNBT(nbt);
        }

        public void invalidate() {
            optional.invalidate();
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getCapabilities().containsKey(IDENTIFIER)) {
                event.addCapability(IDENTIFIER, new Provider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(CAPABILITY).ifPresent(oldState -> {
            event.getEntity().getCapability(CAPABILITY).ifPresent(newState -> {
                newState.copyFrom(oldState);
                if (event.isWasDeath()) {
                    newState.reset();
                }
            });
        });
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(SandevistanState.class);
    }
}
