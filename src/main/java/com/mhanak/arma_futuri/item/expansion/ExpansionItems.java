package com.mhanak.arma_futuri.item.expansion;

import com.mhanak.arma_futuri.item.ExpansionItem;
import com.mhanak.arma_futuri.registry.ModItems;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ExpansionItems {
    public static final ExpansionItem JETPACK = new JetpackItem(new Item.Settings());
    public static final ExpansionItem SHIELD_MODULE = new ShieldModuleItem(new Item.Settings());
    public static final ExpansionItem FLASHLIGHT_UPGRADE = new FlashlightUpgrade(new Item.Settings());
    private static List<ExpansionItem> expansionItems = new ArrayList<>();

    public static ExpansionItem get(int Id) {
        return expansionItems.get(Id);
    }

    //REGISTRATION ORDER SHALL REMAIN UNCHANGED, the ids are assigned incrementally, so if you change the order, the ids will change
    public static void register(String name, ExpansionItem expansionItem) {
        ModItems.register(name, expansionItem);
        expansionItems.add(expansionItem);
    }

    public static int getExtensionId(ExpansionItem expansionItem) {
        for (int i = 0; i < expansionItems.size(); i++) {
            if (expansionItems.get(i).equals(expansionItem)) {
                return i;
            }
        }

        return -1;
    }
}
