package com.mhanak.arma_futuri.item.weapons;

import com.mhanak.arma_futuri.item.WeaponItem;
import com.mhanak.arma_futuri.registry.ModSounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class ChargeRifleItem extends WeaponItem {
    public ChargeRifleItem(Settings settings) {
        super(settings);
    }

    public boolean isSidearm() {return false;}

    public float getSoundRange() {return 2.0f;}

    public float getProjectileSpeed() {return 10;}
    public float getRange() {return 75f;}

    public float getDamage(){
        return 15;
    }

    public float getDamageFalOff(){
        return 0.8f;
    }

    public int shootDelay(){
        return 3;
    }

    public float neededEnergy() {
        return 5;
    }

    public boolean isAutomatic(){
        return true;
    }

    public SoundEvent getFireSound() {
        return ModSounds.CHARGE_RIFLE_SHOOT;
    }

    public float getFovMultiplier(){
        return 0.8f;
    }

    public float getRecoilStrength(){
        return 50;
    }

    public boolean hasScope(){
        return false;
    }

    @Override
    public Vec3d getInHandOffset() {
        return new Vec3d(-0.375D, 0.17D, 0.2D);
    }
}
