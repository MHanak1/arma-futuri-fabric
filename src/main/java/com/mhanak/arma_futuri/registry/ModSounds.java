package com.mhanak.arma_futuri.registry;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static void registerSounds() {

    }

    public static final SoundEvent JETPACK = registerSound("jetpack");
    public static final SoundEvent CHARGE_RIFLE_SHOOT = registerSound("charge_rifle_shot");
    public static final SoundEvent CHARGE_PISTOL_SHOOT = registerSound("charge_pistol_shot");
    public static final SoundEvent CHARGE_MARKSMAN_RIFLE_SHOOT = registerSound("charge_marksman_rifle_shot");
    public static final SoundEvent NOTIFY_RELOADED = registerSound("notify_reloaded");

    // actual registration of all the custom SoundEvents
    private static SoundEvent registerSound(String id) {
        Identifier identifier = ArmaFuturiMod.path(id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

}
