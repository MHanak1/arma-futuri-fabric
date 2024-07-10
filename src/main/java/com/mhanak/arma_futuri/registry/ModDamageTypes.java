package com.mhanak.arma_futuri.registry;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class ModDamageTypes {

    public static final RegistryKey<DamageType> RIFLE_SHOT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, ArmaFuturiMod.path("rifle"));

    private static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    private static DamageSource of(World world, RegistryKey<DamageType> key, Entity attacker) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), attacker);
    }

    private static DamageSource of(World world, RegistryKey<DamageType> key, Entity source, Entity attacker) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), source, attacker);
    }

    public static DamageSource rifle_shot(World world, Entity source) {
        return of(world, RIFLE_SHOT, source);
    }
}
