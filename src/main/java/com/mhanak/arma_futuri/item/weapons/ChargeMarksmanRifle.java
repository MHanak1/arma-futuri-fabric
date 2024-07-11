package com.mhanak.arma_futuri.item.weapons;

import com.mhanak.arma_futuri.item.WeaponItem;
import com.mhanak.arma_futuri.registry.ModSounds;
import net.minecraft.sound.SoundEvent;

public class ChargeMarksmanRifle extends WeaponItem {
    public ChargeMarksmanRifle(Settings settings) {
        super(settings);
    }

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
        return 70;
    }

    @Override
    public boolean hasScope() {
        return true;
    }

    public SoundEvent readyToFireSound (){
        return ModSounds.NOTIFY_RELOADED;
    }
}
