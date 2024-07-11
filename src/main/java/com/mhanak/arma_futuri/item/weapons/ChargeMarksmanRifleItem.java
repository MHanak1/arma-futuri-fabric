package com.mhanak.arma_futuri.item.weapons;

import com.mhanak.arma_futuri.item.WeaponItem;
import com.mhanak.arma_futuri.registry.ModSounds;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class ChargeMarksmanRifleItem extends WeaponItem {
    public ChargeMarksmanRifleItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isSidearm() {return false;}

    @Override
    public float getSoundRange() {return 5.0f;}

    @Override
    public float getProjectileSpeed() {
        return 40;
    }

    @Override
    public float getRange() {
        return 200;
    }

    @Override
    public float getDamageFalOff() {
        return 0.6f;
    }

    @Override
    public float getDamage() {
        return 20;
    }

    @Override
    public SoundEvent getFireSound() {
        return ModSounds.CHARGE_MARKSMAN_RIFLE_SHOOT;
    }

    @Override
    protected int shootDelay() {
        return 30;
    }

    @Override
    public float neededEnergy() {
        return 30;
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

    @Override
    public float getFovMultiplier() {
        return 0.15f;
    }

    @Override
    public float getRecoilStrength() {
        return 100;
    }

    @Override
    public boolean hasScope() {
        return true;
    }

    @Override
    public Vec3d getInHandOffset() {
        return Vec3d.ZERO;
    }

    public SoundEvent readyToFireSound (){
        return ModSounds.NOTIFY_RELOADED;
    }
}
