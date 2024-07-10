package com.mhanak.arma_futuri.sound;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import com.mhanak.arma_futuri.item.ExpansionItem;
import com.mhanak.arma_futuri.registry.ModItems;
import com.mhanak.arma_futuri.registry.ModSounds;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class JetpackSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public JetpackSoundInstance(PlayerEntity player) {
        super(ModSounds.JETPACK, SoundCategory. PLAYERS, SoundInstance.createRandom());
        this.player = player;
        this.attenuationType = SoundInstance.AttenuationType.LINEAR;
        this.repeat = true;
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
        this.volume = 2.0f;
        if (player.isSneaking() && !ArmorData.getJetpackActive((IEntityAccess) player)) {
            this.pitch = 0.8f;
        }
        else this.pitch = 1.0f;
    }
}
