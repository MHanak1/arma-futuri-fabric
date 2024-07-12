package com.mhanak.arma_futuri.item.expansion;

import com.mhanak.arma_futuri.item.ExpansionItem;
import net.minecraft.entity.EquipmentSlot;

public class ShieldModuleItem extends ExpansionItem {
    public ShieldModuleItem(Settings settings) {
        super(settings);
    }

    public boolean canBeEquippedOn(EquipmentSlot slot) {
        return slot == EquipmentSlot.CHEST;
    }
}
