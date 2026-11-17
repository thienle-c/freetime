package com.sandevistan.core;

import com.sandevistan.SandevistanMod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SandevistanMod.MOD_ID);

    public static final Supplier<AttachmentType<SandevistanState>> SANDEVISTAN_STATE =
            ATTACHMENT_TYPES.register("sandevistan_state", () -> AttachmentType.builder(SandevistanState::new)
                    .serialize(SandevistanState.CODEC)
                    .copyOnDeath()
                    .build());
}
