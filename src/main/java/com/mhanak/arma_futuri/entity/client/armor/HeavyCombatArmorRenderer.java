package com.mhanak.arma_futuri.entity.client.armor;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.entity.client.armor.RenderLayer.HeavyCombatArmorColorLayer;
import com.mhanak.arma_futuri.item.armor.HeavyCombatArmorItem;
import net.minecraft.item.DyeableItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class HeavyCombatArmorRenderer extends GeoArmorRenderer<HeavyCombatArmorItem> {
    public HeavyCombatArmorRenderer() {
        super(new DefaultedItemGeoModel<>(ArmaFuturiMod.path("armor/heavy_combat_armor")));

        addRenderLayer(new HeavyCombatArmorColorLayer(this));
    }

    public int getColor(){
        return ((DyeableItem)this.currentStack.getItem()).getColor(currentStack);
    }
}
