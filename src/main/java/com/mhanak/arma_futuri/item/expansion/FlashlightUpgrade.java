package com.mhanak.arma_futuri.item.expansion;

import com.mhanak.arma_futuri.item.ExpansionItem;
import net.minecraft.entity.EquipmentSlot;
public class FlashlightUpgrade extends ExpansionItem {

    public FlashlightUpgrade(Settings settings) {
        super(settings);
    }

    @Override
    public EquipmentSlot getEquipableOn() {
        return EquipmentSlot.HEAD;
    }
}
