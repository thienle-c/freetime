package com.sandevistan.dilation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DilatedEntityData {
    private final UUID entityUuid;
    private final WeakReference<LivingEntity> entityRef;
    private final Set<UUID> sourcePlayerUuids = new HashSet<>();
    private Vec3 lastVelocity;
    private int lastSeenTick;

    public DilatedEntityData(LivingEntity entity, UUID sourcePlayerUuid, int currentTick) {
        this.entityUuid = entity != null ? entity.getUUID() : UUID.randomUUID();
        this.entityRef = new WeakReference<>(entity);
        this.sourcePlayerUuids.add(sourcePlayerUuid);
        this.lastVelocity = entity != null ? entity.getDeltaMovement() : Vec3.ZERO;
        this.lastSeenTick = currentTick;
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public LivingEntity getEntity() {
        return entityRef.get();
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

    public Vec3 getLastVelocity() {
        return lastVelocity;
    }

    public void setLastVelocity(Vec3 lastVelocity) {
        this.lastVelocity = lastVelocity;
    }

    public int getLastSeenTick() {
        return lastSeenTick;
    }

    public void setLastSeenTick(int lastSeenTick) {
        this.lastSeenTick = lastSeenTick;
    }
}
