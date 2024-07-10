package com.mhanak.arma_futuri.util;

import net.minecraft.client.sound.SoundInstance;

public interface IPlayerEntityAccess {
    SoundInstance getJetpackSound();
    void setJetpackSound(SoundInstance sound);
}
