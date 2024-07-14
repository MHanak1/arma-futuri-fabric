package com.mhanak.arma_futuri.item.armor.material;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class HeavyCombatMaterial implements ArmorMaterial {


    @Override
    public int getDurability(ArmorItem.Type type) {
        switch (type){
            case HELMET -> {
                return 1800;
            }
            case CHESTPLATE -> {
                return 2000;
            }
            case LEGGINGS -> {
                return 1900;
            }
            case BOOTS -> {
                return 1700;
            }
        }
        return 0;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        switch (type){
            case HELMET -> {
                return 3;
            }
            case CHESTPLATE -> {
                return 8;
            }
            case LEGGINGS -> {
                return 6;
            }
            case BOOTS -> {
                return 3;
            }
        }
        return 0;

    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null; //Ingredient.ofItems(RegisterItems.X);
    }

    @Override
    public String getName() {return "heavy_combat_armor";}

    @Override
    public float getToughness() {
        return 3.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.2f;
    }
}
