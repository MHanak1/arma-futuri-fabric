package com.mhanak.arma_futuri.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class NotifyReloadedSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public NotifyReloadedSoundInstance(PlayerEntity player, SoundEvent sound) {
        super(sound, SoundCategory. PLAYERS, SoundInstance.createRandom());
        this.volume = 0.5f;
        this.player = player;
        this.attenuationType = AttenuationType.LINEAR;
        this.repeat = false;
    }


    @Override
    public boolean canPlay() {
        return true;
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
