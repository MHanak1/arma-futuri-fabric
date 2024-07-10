package com.mhanak.arma_futuri.mixin;

import com.mhanak.arma_futuri.entity.custom.PersonalShieldEntity;
import com.mhanak.arma_futuri.util.IEntityAccess;
import com.mhanak.arma_futuri.util.ILivingEntityAccess;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntityAccess {
    @Shadow @Nullable public abstract Entity getFirstPassenger();

    @Shadow public abstract String toString();

    @Shadow public abstract boolean equals(Object o);

    @Shadow public abstract boolean shouldRender(double distance);

    @Unique
    //ammount, time, source. order pulled out of my arse.
    private List<Triple<Float, Integer, DamageSource>> schelduedDamage = new ArrayList<>();

    @Unique
    private NbtCompound persistentData;
    @Unique
    private AreaLight areaLight;

    @Unique
    private boolean isAiming;

    public boolean isAiming() {
        return isAiming;
    }

    public void setAiming(boolean isAiming) {
        this.isAiming = isAiming;
    }

    @Unique
    public void arma_futuri$scheldueDamage(DamageSource source, int time, float amount) {
        schelduedDamage.add(Triple.of(amount, time, source));
    }

    @Unique
    public AreaLight arma_futuri$getAreaLight(){
        return areaLight;
    }

    @Unique
    public void arma_futuri$setAreaLight(AreaLight newAreaLight){
        this.areaLight = newAreaLight;
    }

    @Override
    public NbtCompound arma_futuri$getPersistentData() {
        if (this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return this.persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void writePersistentData(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if (this.persistentData != null) {
            nbt.put("arma_futuri.kaupen_data", this.persistentData);
        }
    }
    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void readPersistentData(NbtCompound nbt, CallbackInfo ci) {
        persistentData = nbt.getCompound("arma_futuri.kaupen_data");
    }


    @Inject(method = "tick", at = @At("HEAD"))
    protected void tick(CallbackInfo ci) {

        for (int i = 0; i < this.schelduedDamage.size(); i++) {
            Triple<Float, Integer, DamageSource> ammount = schelduedDamage.get(i);
            ammount = Triple.of(ammount.getLeft(), ammount.getMiddle() -1, ammount.getRight());
            schelduedDamage.set(i, ammount);

            if (ammount.getMiddle() <= 0) {
                ((Entity)(Object)this).damage(ammount.getRight(), ammount.getLeft());
                schelduedDamage.remove(i);
                i--;
            }
        }

        if ((Entity)(Object)this instanceof LivingEntity) {
            if (!(((ILivingEntityAccess)this).getAttachedEntity() instanceof PersonalShieldEntity)) {
                NbtCompound nbt = ((IEntityAccess) this).arma_futuri$getPersistentData();
                float shieldHealth = nbt.getFloat("ShieldHealth");
                if (shieldHealth < 30) {
                    nbt.putFloat("ShieldHealth", shieldHealth + 0.1f);
                }
            }
        }
    }
}
