package com.mhanak.arma_futuri.item.armor.material;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class LightCombatMaterial implements ArmorMaterial {


    @Override
    public int getDurability(ArmorItem.Type type) {
        switch (type){
            case HELMET -> {
                return 1300;
            }
            case CHESTPLATE -> {
                return 1500;
            }
            case LEGGINGS -> {
                return 1600;
            }
            case BOOTS -> {
                return 1100;
            }
        }
        return 0;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        switch (type){
            case HELMET -> {
                return 2;
            }
            case CHESTPLATE -> {
                return 7;
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
    public String getName() {return "light_combat_armor";}

    @Override
    public float getToughness() {
        return 2.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.1f;
    }
}
