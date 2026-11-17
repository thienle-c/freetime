package com.sandevistan.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {

    public static class Common {
        public final ForgeConfigSpec.IntValue activeDurationTicks;
        public final ForgeConfigSpec.IntValue cooldownTicks;
        public final ForgeConfigSpec.DoubleValue speedMultiplier;

        // Phase 2: Dash Configuration
        public final ForgeConfigSpec.BooleanValue dashEnabled;
        public final ForgeConfigSpec.IntValue dashCooldownTicks;
        public final ForgeConfigSpec.DoubleValue dashSpeed;
        public final ForgeConfigSpec.DoubleValue dashDistance;
        public final ForgeConfigSpec.BooleanValue dashAirEnabled;
        public final ForgeConfigSpec.IntValue dashMaxCharges;

        // Phase 3: Time Dilation Configuration
        public final ForgeConfigSpec.BooleanValue timeDilationEnabled;
        public final ForgeConfigSpec.DoubleValue timeDilationRadius;
        public final ForgeConfigSpec.DoubleValue timeDilationFactor;
        public final ForgeConfigSpec.BooleanValue affectHostileMobs;
        public final ForgeConfigSpec.BooleanValue affectPassiveMobs;
        public final ForgeConfigSpec.BooleanValue affectProjectiles;
        public final ForgeConfigSpec.BooleanValue affectOtherPlayers;
        public final ForgeConfigSpec.IntValue dilationScanIntervalTicks;
        public final ForgeConfigSpec.IntValue maxAffectedEntities;

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

            // Dash settings
            builder.comment("Phase 2: Directional Dash Configuration")
                   .push("dash");

            dashEnabled = builder
                    .comment("Whether dash ability is enabled when Sandevistan is ACTIVE. Default: true.")
                    .define("dashEnabled", true);

            dashCooldownTicks = builder
                    .comment("Cooldown ticks required to recharge one dash charge (20 ticks = 1 second). Default: 20 (1 second).")
                    .defineInRange("dashCooldownTicks", 20, 1, 200);

            dashSpeed = builder
                    .comment("Base horizontal velocity impulse magnitude for dash. Default: 1.25.")
                    .defineInRange("dashSpeed", 1.25, 0.2, 5.0);

            dashDistance = builder
                    .comment("Maximum effective dash distance in blocks before stopping or collision check. Default: 5.0.")
                    .defineInRange("dashDistance", 5.0, 1.0, 20.0);

            dashAirEnabled = builder
                    .comment("Allow dashing while airborne (air dash). Default: true.")
                    .define("dashAirEnabled", true);

            dashMaxCharges = builder
                    .comment("Maximum number of stored dash charges when Sandevistan is ACTIVE. Default: 2.")
                    .defineInRange("dashMaxCharges", 2, 1, 5);

            builder.pop();

            // Time Dilation settings
            builder.comment("Phase 3: Local Time Dilation Configuration")
                   .push("timeDilation");

            timeDilationEnabled = builder
                    .comment("Whether local time dilation field is generated around active Sandevistan players. Default: true.")
                    .define("timeDilationEnabled", true);

            timeDilationRadius = builder
                    .comment("Radius of the local time dilation sphere around the player in blocks. Default: 24.0.")
                    .defineInRange("timeDilationRadius", 24.0, 4.0, 64.0);

            timeDilationFactor = builder
                    .comment("Time dilation factor (0.2 = 5x slower, 1.0 = normal speed). Default: 0.2.")
                    .defineInRange("timeDilationFactor", 0.2, 0.05, 0.95);

            affectHostileMobs = builder
                    .comment("Whether time dilation slows down hostile mobs. Default: true.")
                    .define("affectHostileMobs", true);

            affectPassiveMobs = builder
                    .comment("Whether time dilation slows down passive and neutral animals. Default: true.")
                    .define("affectPassiveMobs", true);

            affectProjectiles = builder
                    .comment("Whether time dilation slows down flying projectiles (arrows, tridents, fireballs, etc.). Default: true.")
                    .define("affectProjectiles", true);

            affectOtherPlayers = builder
                    .comment("Whether time dilation slows down other non-Sandevistan players in multiplayer. Default: false.")
                    .define("affectOtherPlayers", false);

            dilationScanIntervalTicks = builder
                    .comment("Interval in ticks between spatial entity scans around active players. Default: 2.")
                    .defineInRange("dilationScanIntervalTicks", 2, 1, 20);

            maxAffectedEntities = builder
                    .comment("Maximum number of entities a single player's dilation field can track simultaneously. Default: 64.")
                    .defineInRange("maxAffectedEntities", 64, 8, 256);

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
