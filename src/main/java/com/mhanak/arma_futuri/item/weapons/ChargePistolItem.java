package com.mhanak.arma_futuri.item.weapons;

import com.mhanak.arma_futuri.item.WeaponItem;
import com.mhanak.arma_futuri.registry.ModSounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class ChargePistolItem extends WeaponItem {
    public ChargePistolItem(Settings settings) {
        super(settings);
    }

    @Override
    public float getProjectileSpeed() {
        return 15;
    }

    @Override
    public float getRange() {
        return 50;
    }

    @Override
    public boolean isSidearm() {
        return true;
    }

    @Override
    public float getDamageFalOff() {
        return 0.8f;
    }

    @Override
    public float getDamage() {
        return 9;
    }

    @Override
    public float getSoundRange() {
        return 1.5f;
    }

    @Override
    public SoundEvent getFireSound() {
        return ModSounds.CHARGE_PISTOL_SHOOT;
    }

    @Override
    protected int shootDelay() {
        return 2;
    }

    @Override
    public float neededEnergy() {
        return 3;
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

    @Override
    public float getFovMultiplier() {
        return 0.8f;
    }

    @Override
    public float getRecoilStrength() {
        return 60;
    }

    @Override
    public boolean hasScope() {
        return false;
    }

    @Override
    public Vec3d getInHandOffset() {
        return new Vec3d(-0.375D, 0.17D, -0.1D);
    }
}
