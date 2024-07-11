package com.mhanak.arma_futuri.entity.client.armor;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.item.armor.LightCombatArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class HeavyCombatArmorRenderer extends GeoArmorRenderer<LightCombatArmorItem> {
    public HeavyCombatArmorRenderer() {
        super(new DefaultedItemGeoModel<>(ArmaFuturiMod.path("armor/light_combat_armor")));
    }
}
