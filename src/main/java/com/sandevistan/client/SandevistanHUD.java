package com.sandevistan.client;

import com.sandevistan.config.ModConfig;
import com.sandevistan.core.SandevistanState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SandevistanHUD implements IGuiOverlay {
    public static final SandevistanHUD INSTANCE = new SandevistanHUD();

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!ModConfig.CLIENT.hudEnabled.get()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) {
            return;
        }

        Font font = mc.font;
        SandevistanState state = SandevistanClient.getClientState();

        int x = ModConfig.CLIENT.hudX.get();
        int y = ModConfig.CLIENT.hudY.get();
        int width = 140;
        int height = 24;

        int borderColor = switch (state.getState()) {
            case ACTIVE -> 0xFF00FF66;   // Neon green
            case COOLDOWN -> 0xFFFF5500; // Cyber amber/orange
            default -> 0xFF00F3FF;       // Cyan
        };

        // Background panel
        guiGraphics.fill(x, y, x + width, y + height, 0xBB0B1015);
        // Border outline
        guiGraphics.renderOutline(x, y, width, height, borderColor);

        if (state.isReady()) {
            String text = "SANDEVISTAN: READY";
            guiGraphics.drawString(font, text, x + 8, y + 8, 0x00F3FF, true);
        } else if (state.isActive()) {
            float seconds = Math.max(0f, state.getActiveTicksRemaining() / 20.0f);
            String text = String.format("SANDEVISTAN: %.1fs", seconds);
            guiGraphics.drawString(font, text, x + 8, y + 4, 0x00FF66, true);

            float progress = state.getTotalActiveTicks() > 0
                    ? (float) state.getActiveTicksRemaining() / state.getTotalActiveTicks()
                    : 0f;
            int barWidth = (int) ((width - 16) * Math.max(0f, Math.min(1f, progress)));
            guiGraphics.fill(x + 8, y + 16, x + width - 8, y + 19, 0x4400FF66);
            if (barWidth > 0) {
                guiGraphics.fill(x + 8, y + 16, x + 8 + barWidth, y + 19, 0xFF00FF66);
            }
        } else if (state.isCooldown()) {
            float seconds = Math.max(0f, state.getCooldownTicksRemaining() / 20.0f);
            String text = String.format("COOLDOWN: %.1fs", seconds);
            guiGraphics.drawString(font, text, x + 8, y + 4, 0xFFFF5500, true);

            float progress = state.getTotalCooldownTicks() > 0
                    ? 1.0f - ((float) state.getCooldownTicksRemaining() / state.getTotalCooldownTicks())
                    : 0f;
            int barWidth = (int) ((width - 16) * Math.max(0f, Math.min(1f, progress)));
            guiGraphics.fill(x + 8, y + 16, x + width - 8, y + 19, 0x44FF5500);
            if (barWidth > 0) {
                guiGraphics.fill(x + 8, y + 16, x + 8 + barWidth, y + 19, 0xFFFF5500);
            }
        }
    }
}
