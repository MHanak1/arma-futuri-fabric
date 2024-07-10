package com.mhanak.arma_futuri.util;

import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;

public interface IEntityAccess {
    NbtCompound arma_futuri$getPersistentData();
    AreaLight arma_futuri$getAreaLight();
    void arma_futuri$setAreaLight(AreaLight newAreaLight);
    void arma_futuri$scheldueDamage(DamageSource source, int time, float amount);
    void setAiming(boolean aiming);
    boolean isAiming();

}
