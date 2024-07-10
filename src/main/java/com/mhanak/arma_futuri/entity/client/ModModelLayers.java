package com.mhanak.arma_futuri.entity.client;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer PERSONAL_SHIELD =
            new EntityModelLayer(Identifier.of(ArmaFuturiMod.MODID, "personal_shield"), "outer");

    public static final EntityModelLayer RIFLE_PROJECTILE =
            new EntityModelLayer(ArmaFuturiMod.path("rifle_projectile"), "outer");
}
