package com.mhanak.arma_futuri.item.expansion;

import com.mhanak.arma_futuri.item.ExpansionItem;
import net.minecraft.entity.EquipmentSlot;

public class OxygenRecyclerUpgrade extends ExpansionItem {

    public OxygenRecyclerUpgrade(Settings settings) {
        super(settings);
    }

    public boolean canBeEquippedOn(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD;
    }

    @Override
    public float oxygenUsageMultiplier() {
        return 0;
    }
}
