package com.sandevistan.dilation;

import com.sandevistan.SandevistanMod;
import com.sandevistan.config.ModConfig;
import com.sandevistan.core.SandevistanCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = SandevistanMod.MOD_ID)
public class TimeDilationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeDilationController.class);

    private static final Set<UUID> ACTIVE_SANDEVISTAN_PLAYERS = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, DilatedEntityData> ACTIVE_DILATED_ENTITIES = new ConcurrentHashMap<>();
    private static final Map<UUID, DilatedProjectileData> ACTIVE_DILATED_PROJECTILES = new ConcurrentHashMap<>();

    private static int serverTickCounter = 0;

    public static void registerActivePlayer(ServerPlayer player) {
        ACTIVE_SANDEVISTAN_PLAYERS.add(player.getUUID());
        LOGGER.debug("Registered active dilation sphere for player {}", player.getScoreboardName());
    }

    public static void unregisterActivePlayer(ServerPlayer player) {
        ACTIVE_SANDEVISTAN_PLAYERS.remove(player.getUUID());
        sweepPlayerSources(player.getUUID(), ModConfig.COMMON.timeDilationFactor.get());
        LOGGER.debug("Unregistered dilation sphere for player {}", player.getScoreboardName());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        serverTickCounter++;

        if (!ModConfig.COMMON.timeDilationEnabled.get()) {
            if (!ACTIVE_DILATED_ENTITIES.isEmpty() || !ACTIVE_DILATED_PROJECTILES.isEmpty()) {
                clearAll();
            }
            return;
        }

        double factor = ModConfig.COMMON.timeDilationFactor.get();

        // 1. Tick flying dilated projectiles for gravity compensation
        for (Iterator<Map.Entry<UUID, DilatedProjectileData>> it = ACTIVE_DILATED_PROJECTILES.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, DilatedProjectileData> entry = it.next();
            DilatedProjectileData data = entry.getValue();
            Projectile proj = data.getProjectile();
            if (proj == null || !proj.isAlive() || proj.isRemoved()) {
                it.remove();
            } else {
                ProjectileDilationHandler.tickDilation(proj, factor);
            }
        }

        // 2. Perform periodic spatial sweep & resolution
        int scanInterval = ModConfig.COMMON.dilationScanIntervalTicks.get();
        if (serverTickCounter % scanInterval == 0) {
            performFieldSweep(factor);
        }
    }

    /**
     * Scans around active Sandevistan players to capture surrounding mobs and projectiles.
     */
    public static void scanSurroundings(ServerPlayer player) {
        if (!ModConfig.COMMON.timeDilationEnabled.get()) {
            return;
        }

        double radius = ModConfig.COMMON.timeDilationRadius.get();
        double radiusSqr = radius * radius;
        double factor = ModConfig.COMMON.timeDilationFactor.get();
        int maxEntities = ModConfig.COMMON.maxAffectedEntities.get();

        ServerLevel level = player.serverLevel();
        AABB searchBox = player.getBoundingBox().inflate(radius);

        List<Entity> nearbyEntities = level.getEntities(
                player,
                searchBox,
                e -> e.isAlive() && !e.isRemoved() && e.distanceToSqr(player) <= radiusSqr
        );

        int count = 0;
        for (Entity target : nearbyEntities) {
            if (count >= maxEntities) {
                break;
            }

            if (target instanceof LivingEntity living) {
                if (isEligibleLiving(living, player)) {
                    DilatedEntityData data = ACTIVE_DILATED_ENTITIES.computeIfAbsent(
                            living.getUUID(),
                            k -> {
                                EntityDilationHandler.applyDilation(living, factor);
                                return new DilatedEntityData(living, player.getUUID(), serverTickCounter);
                            }
                    );
                    data.addSourcePlayer(player.getUUID());
                    data.setLastSeenTick(serverTickCounter);
                    count++;
                }
            } else if (target instanceof Projectile projectile) {
                if (ModConfig.COMMON.affectProjectiles.get()) {
                    DilatedProjectileData data = ACTIVE_DILATED_PROJECTILES.computeIfAbsent(
                            projectile.getUUID(),
                            k -> {
                                DilatedProjectileData newData = new DilatedProjectileData(projectile, player.getUUID(), serverTickCounter);
                                ProjectileDilationHandler.applyDilation(projectile, newData, factor);
                                return newData;
                            }
                    );
                    data.addSourcePlayer(player.getUUID());
                    data.setLastSeenTick(serverTickCounter);
                    count++;
                }
            }
        }
    }

    private static boolean isEligibleLiving(LivingEntity living, ServerPlayer sourcePlayer) {
        if (living == sourcePlayer) {
            return false;
        }

        if (living instanceof Player otherPlayer) {
            if (!ModConfig.COMMON.affectOtherPlayers.get()) {
                return false;
            }
            // Players with active Sandevistan are immune to slowdown
            return otherPlayer.getCapability(SandevistanCapability.CAPABILITY)
                    .map(state -> !state.isActive())
                    .orElse(true);
        }

        if (living instanceof Monster) {
            return ModConfig.COMMON.affectHostileMobs.get();
        }

        if (living instanceof Animal || living instanceof AmbientCreature) {
            return ModConfig.COMMON.affectPassiveMobs.get();
        }

        return true;
    }

    /**
     * Sweeps all tracked entities to verify they are still in range of at least one active Sandevistan player.
     */
    private static void performFieldSweep(double factor) {
        double radius = ModConfig.COMMON.timeDilationRadius.get();
        double radiusSqr = radius * radius;

        // Sweep living entities
        for (Iterator<Map.Entry<UUID, DilatedEntityData>> it = ACTIVE_DILATED_ENTITIES.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, DilatedEntityData> entry = it.next();
            DilatedEntityData data = entry.getValue();
            LivingEntity living = data.getEntity();

            if (living == null || !living.isAlive() || living.isRemoved()) {
                it.remove();
                continue;
            }

            // Remove sources that are no longer active or out of range
            data.getSourcePlayerUuids().removeIf(sourceUuid -> {
                if (!ACTIVE_SANDEVISTAN_PLAYERS.contains(sourceUuid)) {
                    return true;
                }
                Player p = living.level().getPlayerByUUID(sourceUuid);
                return p == null || !p.isAlive() || living.distanceToSqr(p) > radiusSqr;
            });

            if (!data.hasSources()) {
                EntityDilationHandler.removeDilation(living);
                it.remove();
            }
        }

        // Sweep projectiles
        if (ModConfig.COMMON.affectProjectiles.get()) {
            for (Iterator<Map.Entry<UUID, DilatedProjectileData>> it = ACTIVE_DILATED_PROJECTILES.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<UUID, DilatedProjectileData> entry = it.next();
                DilatedProjectileData data = entry.getValue();
                Projectile projectile = data.getProjectile();

                if (projectile == null || !projectile.isAlive() || projectile.isRemoved()) {
                    it.remove();
                    continue;
                }

                data.getSourcePlayerUuids().removeIf(sourceUuid -> {
                    if (!ACTIVE_SANDEVISTAN_PLAYERS.contains(sourceUuid)) {
                        return true;
                    }
                    Player p = projectile.level().getPlayerByUUID(sourceUuid);
                    return p == null || !p.isAlive() || projectile.distanceToSqr(p) > radiusSqr;
                });

                if (!data.hasSources()) {
                    ProjectileDilationHandler.restoreDilation(projectile, data, factor);
                    it.remove();
                }
            }
        }
    }

    /**
     * Removes a player from all tracked entities' source sets upon deactivation.
     */
    private static void sweepPlayerSources(UUID playerUuid, double factor) {
        for (Iterator<Map.Entry<UUID, DilatedEntityData>> it = ACTIVE_DILATED_ENTITIES.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, DilatedEntityData> entry = it.next();
            DilatedEntityData data = entry.getValue();
            data.removeSourcePlayer(playerUuid);
            if (!data.hasSources()) {
                LivingEntity living = data.getEntity();
                if (living != null && living.isAlive()) {
                    EntityDilationHandler.removeDilation(living);
                }
                it.remove();
            }
        }

        for (Iterator<Map.Entry<UUID, DilatedProjectileData>> it = ACTIVE_DILATED_PROJECTILES.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, DilatedProjectileData> entry = it.next();
            DilatedProjectileData data = entry.getValue();
            data.removeSourcePlayer(playerUuid);
            if (!data.hasSources()) {
                Projectile proj = data.getProjectile();
                if (proj != null && proj.isAlive()) {
                    ProjectileDilationHandler.restoreDilation(proj, data, factor);
                }
                it.remove();
            }
        }
    }

    public static void clearAll() {
        double factor = ModConfig.COMMON.timeDilationFactor.get();
        for (DilatedEntityData data : ACTIVE_DILATED_ENTITIES.values()) {
            LivingEntity living = data.getEntity();
            if (living != null && living.isAlive()) {
                EntityDilationHandler.removeDilation(living);
            }
        }
        ACTIVE_DILATED_ENTITIES.clear();

        for (DilatedProjectileData data : ACTIVE_DILATED_PROJECTILES.values()) {
            Projectile proj = data.getProjectile();
            if (proj != null && proj.isAlive()) {
                ProjectileDilationHandler.restoreDilation(proj, data, factor);
            }
        }
        ACTIVE_DILATED_PROJECTILES.clear();
        ACTIVE_SANDEVISTAN_PLAYERS.clear();
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity != null) {
            ACTIVE_DILATED_ENTITIES.remove(entity.getUUID());
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity != null) {
            ACTIVE_DILATED_ENTITIES.remove(entity.getUUID());
            ACTIVE_DILATED_PROJECTILES.remove(entity.getUUID());
            if (entity instanceof Player player) {
                ACTIVE_SANDEVISTAN_PLAYERS.remove(player.getUUID());
            }
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (projectile != null) {
            DilatedProjectileData data = ACTIVE_DILATED_PROJECTILES.remove(projectile.getUUID());
            if (data != null && data.isDilated()) {
                ProjectileDilationHandler.restoreDilation(projectile, data, ModConfig.COMMON.timeDilationFactor.get());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            unregisterActivePlayer(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            unregisterActivePlayer(player);
        }
    }

    public static Map<UUID, DilatedEntityData> getActiveDilatedEntities() {
        return ACTIVE_DILATED_ENTITIES;
    }

    public static Map<UUID, DilatedProjectileData> getActiveDilatedProjectiles() {
        return ACTIVE_DILATED_PROJECTILES;
    }

    public static Set<UUID> getActiveSandevistanPlayers() {
        return ACTIVE_SANDEVISTAN_PLAYERS;
    }
}
