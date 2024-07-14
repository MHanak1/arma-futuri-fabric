package com.mhanak.arma_futuri.item.expansion;

import com.mhanak.arma_futuri.item.ExpansionItem;
import net.minecraft.entity.EquipmentSlot;

public class ArmorReinforcementUpgrade extends ExpansionItem {

    public ArmorReinforcementUpgrade(Settings settings) {
        super(settings);
    }

    public boolean canBeEquippedOn(EquipmentSlot slot) {
        return true;
    }

    @Override
    public float addsProtection() {
        return 0.7f;
    }

    public float speedModifier() {
        return -0.05f;
    }

    @Override
    public float addsWheight() {
        return 0.1f;
    }
}
