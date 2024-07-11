package com.mhanak.arma_futuri.registry;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.item.armor.HeavyCombatArmorItem;
import com.mhanak.arma_futuri.item.armor.LightCombatArmorItem;
import com.mhanak.arma_futuri.item.armor.material.HeavyCombatMaterial;
import com.mhanak.arma_futuri.item.armor.material.LightCombatMaterial;
import com.mhanak.arma_futuri.item.expansion.ExpansionItems;
import com.mhanak.arma_futuri.item.weapons.ChargePistolItem;
import com.mhanak.arma_futuri.item.weapons.ChargeRifleItem;
import com.mhanak.arma_futuri.item.weapons.ChargeMarksmanRifleItem;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class ModItems {

    public static final Set<Item> ITEMS = new HashSet<>();

    public static final ArmorMaterial LIGHT_COMBAT_ARMOR_MATERIAL = new LightCombatMaterial();
    public static final ArmorMaterial HEAVY_COMBAT_ARMOR_MATERIAL = new HeavyCombatMaterial();

    public static final Item LIGHT_COMBAT_HELMET = new LightCombatArmorItem(LIGHT_COMBAT_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings());
    public static final Item LIGHT_COMBAT_CHESTPLATE = new LightCombatArmorItem(LIGHT_COMBAT_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings());
    public static final Item LIGHT_COMBAT_LEGGINGS = new LightCombatArmorItem(LIGHT_COMBAT_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings());
    public static final Item LIGHT_COMBAT_BOOTS = new LightCombatArmorItem(LIGHT_COMBAT_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings());

    public static final Item HEAVY_COMBAT_HELMET = new HeavyCombatArmorItem(HEAVY_COMBAT_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings());
    public static final Item HEAVY_COMBAT_CHESTPLATE = new HeavyCombatArmorItem(HEAVY_COMBAT_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings());
    public static final Item HEAVY_COMBAT_LEGGINGS = new HeavyCombatArmorItem(HEAVY_COMBAT_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings());
    public static final Item HEAVY_COMBAT_BOOTS = new HeavyCombatArmorItem(HEAVY_COMBAT_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings());

    public static final Item CHARGE_RIFLE = new ChargeRifleItem(new Item.Settings());
    public static final Item CHARGE_MARKSMAN_RIFLE = new ChargeMarksmanRifleItem(new Item.Settings());
    public static final Item CHARGE_PISTOL = new ChargePistolItem(new Item.Settings());

    public static void bootstrap() {


        register("light_combat_helmet", LIGHT_COMBAT_HELMET);
        register("light_combat_chestplate", LIGHT_COMBAT_CHESTPLATE);
        register("light_combat_leggings", LIGHT_COMBAT_LEGGINGS);
        register("light_combat_boots", LIGHT_COMBAT_BOOTS);

        register("heavy_combat_helmet", HEAVY_COMBAT_HELMET);
        register("heavy_combat_chestplate", HEAVY_COMBAT_CHESTPLATE);
        register("heavy_combat_leggings", HEAVY_COMBAT_LEGGINGS);
        register("heavy_combat_boots", HEAVY_COMBAT_BOOTS);

        register("charge_rifle", CHARGE_RIFLE);
        register("charge_marksman_rifle", CHARGE_MARKSMAN_RIFLE);
        register("charge_pistol", CHARGE_PISTOL);

        ExpansionItems.register("jetpack", ExpansionItems.JETPACK);
        ExpansionItems.register("shield_module", ExpansionItems.SHIELD_MODULE);
        ExpansionItems.register("flashlight_upgrade", ExpansionItems.FLASHLIGHT_UPGRADE);

    }

    public static <T extends Item> T register(String name, T item) {
        T value = Registry.register(Registries.ITEM, ArmaFuturiMod.path(name), item);
        ITEMS.add(value);
        return value;
    }

    public static void fillTab(ItemGroup.DisplayContext context, ItemGroup.Entries output) {
        ITEMS.stream().sorted(Comparator.comparing(Registries.ITEM::getId)).forEach(item -> output.add(new ItemStack(item)));
    }
}
