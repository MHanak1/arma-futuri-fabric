package com.mhanak.arma_futuri.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class RifleSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public RifleSoundInstance(PlayerEntity player, SoundEvent sound, float range) {
        super(sound, SoundCategory. PLAYERS, SoundInstance.createRandom());
        this.volume = range;
        this.player = player;
        this.attenuationType = AttenuationType.LINEAR;
        this.repeat = false;
    }


    @Override
    public boolean canPlay() {
        return !this.player.isSilent();
    }

    @Override
    public void tick() {
        if (this.player.isRemoved()) {
            this.setDone();
            return;
        }
        this.x = (float)this.player.getX();
        this.y = (float)this.player.getY();
        this.z = (float)this.player.getZ();
        this.pitch = 1.0f;
    }
}
