package com.sandevistan.dilation;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DilatedProjectileData {
    private final UUID projectileUuid;
    private final WeakReference<Projectile> projectileRef;
    private final Set<UUID> sourcePlayerUuids = new HashSet<>();
    private Vec3 preDilationVelocity;
    private boolean isDilated = false;
    private int lastSeenTick;

    public DilatedProjectileData(Projectile projectile, UUID sourcePlayerUuid, int currentTick) {
        this.projectileUuid = projectile != null ? projectile.getUUID() : UUID.randomUUID();
        this.projectileRef = new WeakReference<>(projectile);
        this.sourcePlayerUuids.add(sourcePlayerUuid);
        this.preDilationVelocity = projectile != null ? projectile.getDeltaMovement() : Vec3.ZERO;
        this.lastSeenTick = currentTick;
    }

    public UUID getProjectileUuid() {
        return projectileUuid;
    }

    public Projectile getProjectile() {
        return projectileRef.get();
    }

    public Set<UUID> getSourcePlayerUuids() {
        return sourcePlayerUuids;
    }

    public void addSourcePlayer(UUID playerUuid) {
        sourcePlayerUuids.add(playerUuid);
    }

    public void removeSourcePlayer(UUID playerUuid) {
        sourcePlayerUuids.remove(playerUuid);
    }

    public boolean hasSources() {
        return !sourcePlayerUuids.isEmpty();
    }

    public Vec3 getPreDilationVelocity() {
        return preDilationVelocity;
    }

    public void setPreDilationVelocity(Vec3 preDilationVelocity) {
        this.preDilationVelocity = preDilationVelocity;
    }

    public boolean isDilated() {
        return isDilated;
    }

    public void setDilated(boolean dilated) {
        isDilated = dilated;
    }

    public int getLastSeenTick() {
        return lastSeenTick;
    }

    public void setLastSeenTick(int lastSeenTick) {
        this.lastSeenTick = lastSeenTick;
    }
}
