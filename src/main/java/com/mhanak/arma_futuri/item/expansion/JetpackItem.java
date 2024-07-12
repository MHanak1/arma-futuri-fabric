package com.mhanak.arma_futuri.item.expansion;

import com.mhanak.arma_futuri.item.ExpansionItem;
import net.minecraft.entity.EquipmentSlot;

public class JetpackItem extends ExpansionItem {

    public JetpackItem(Settings settings) {
        super(settings);
    }

    public boolean canBeEquippedOn(EquipmentSlot slot) {
        return slot == EquipmentSlot.CHEST;
    }

    @Override
    public float addsFuel() {
        return 40f;
    }
}
