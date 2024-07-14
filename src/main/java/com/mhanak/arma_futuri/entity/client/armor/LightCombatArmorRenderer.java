package com.mhanak.arma_futuri.entity.client.armor;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.entity.client.armor.RenderLayer.LightCombatArmorColorLayer;
import com.mhanak.arma_futuri.item.armor.LightCombatArmorItem;
import net.minecraft.item.DyeableItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class LightCombatArmorRenderer extends GeoArmorRenderer<LightCombatArmorItem> {
    public LightCombatArmorRenderer() {
        super(new DefaultedItemGeoModel<>(ArmaFuturiMod.path("armor/light_combat_armor")));

        addRenderLayer(new LightCombatArmorColorLayer(this));
    }
    public int getColor(){
        return ((DyeableItem)this.currentStack.getItem()).getColor(currentStack);
    }

}
