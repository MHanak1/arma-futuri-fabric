package com.mhanak.arma_futuri.item.expansion;

import com.mhanak.arma_futuri.item.ExpansionItem;
import net.minecraft.entity.EquipmentSlot;

public class ShockAbsorberUpgrade extends ExpansionItem {

    public ShockAbsorberUpgrade(Settings settings) {
        super(settings);
    }

    public boolean canBeEquippedOn(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    @Override
    public float fallDamageMultiplier() {
        return 0.2f;
    }
}
