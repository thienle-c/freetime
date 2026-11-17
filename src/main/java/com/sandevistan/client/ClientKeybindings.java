package com.sandevistan.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ClientKeybindings {
    public static final String KEY_CATEGORY = "key.sandevistan.category";

    public static final KeyMapping KEY_ACTIVATE = new KeyMapping(
            "key.sandevistan.activate",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            KEY_CATEGORY
    );

    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY_ACTIVATE);
    }
}
