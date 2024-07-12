package com.mhanak.arma_futuri.item.expansion;

import com.mhanak.arma_futuri.item.ExpansionItem;
import net.minecraft.entity.EquipmentSlot;

public class HydraulicsUpgrade extends ExpansionItem {

    public HydraulicsUpgrade(Settings settings) {
        super(settings);
    }

    public boolean canBeEquippedOn(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    @Override
    public float speedModifier() {return 0.5f;}
}
