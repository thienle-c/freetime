package com.sandevistan.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {

    public static class Common {
        public final ForgeConfigSpec.IntValue activeDurationTicks;
        public final ForgeConfigSpec.IntValue cooldownTicks;
        public final ForgeConfigSpec.DoubleValue speedMultiplier;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Sandevistan Overdrive - Server & Gameplay Configuration")
                   .push("gameplay");

            activeDurationTicks = builder
                    .comment("Duration of active Sandevistan state in ticks (20 ticks = 1 second). Default: 160 (8 seconds).")
                    .defineInRange("activeDurationTicks", 160, 20, 1200);

            cooldownTicks = builder
                    .comment("Cooldown duration after Sandevistan deactivation in ticks (20 ticks = 1 second). Default: 400 (20 seconds).")
                    .defineInRange("cooldownTicks", 400, 20, 6000);

            speedMultiplier = builder
                    .comment("Movement speed multiplier bonus added to base speed (e.g. 3.0 = +300% speed, total 4x speed). Default: 3.0.")
                    .defineInRange("speedMultiplier", 3.0, 0.1, 20.0);

            builder.pop();
        }
    }

    public static class Client {
        public final ForgeConfigSpec.BooleanValue hudEnabled;
        public final ForgeConfigSpec.IntValue hudX;
        public final ForgeConfigSpec.IntValue hudY;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Sandevistan Overdrive - Client Configuration")
                   .push("hud");

            hudEnabled = builder
                    .comment("Whether to render the Sandevistan HUD overlay on screen. Default: true.")
                    .define("hudEnabled", true);

            hudX = builder
                    .comment("X coordinate offset for Sandevistan HUD overlay. Default: 10.")
                    .defineInRange("hudX", 10, 0, 3840);

            hudY = builder
                    .comment("Y coordinate offset for Sandevistan HUD overlay. Default: 10.")
                    .defineInRange("hudY", 10, 0, 2160);

            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Client CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();

        Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
    }
}
