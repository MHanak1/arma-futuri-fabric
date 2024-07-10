package com.mhanak.arma_futuri.registry;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.ArmaFuturiModClient;
import com.mhanak.arma_futuri.entity.custom.PersonalShieldEntity;

import com.mhanak.arma_futuri.entity.projectile.RifleProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {


    public static final EntityType<PersonalShieldEntity> PERSONAL_SHIELD = Registry.register(
        Registries.ENTITY_TYPE,
        ArmaFuturiMod.path("personal_shield"),
        EntityType.Builder.create(PersonalShieldEntity::new, SpawnGroup.MISC)
                //.dimensions(2.5f, 2.5f) /1.21
                .setDimensions(2.5f, 2.5f)
                .build("personal_shield"));

    public static final EntityType<RifleProjectileEntity> RIFLE_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            ArmaFuturiMod.path("rifle_projectile"),
            EntityType.Builder.create(RifleProjectileEntity::new, SpawnGroup.MISC)
                    .setDimensions(0.1f, 0.1f)
                    .build("rifle_projectile"));


    public static void bootstrap() {
        FabricDefaultAttributeRegistry.register(ModEntities.PERSONAL_SHIELD, PersonalShieldEntity.createPersonalShieldAttributes());
        ArmaFuturiMod.LOGGER.debug("Personal Shield Entity Registered");
    }

}
