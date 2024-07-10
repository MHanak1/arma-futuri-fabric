package com.mhanak.arma_futuri.item;

import com.mhanak.arma_futuri.item.expansion.ExpansionItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ArmorItemWithExpansions extends ArmorItem {
    public ArmorItemWithExpansions(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType == ClickType.RIGHT) {
            if (!slot.hasStack() && slot.isEnabled()) {
                ExpansionItem lastExpansion = unequipExpansion(stack);
                if (lastExpansion != null) {
                    System.out.println("Expansion removed");
                    slot.insertStack(lastExpansion.getDefaultStack());
                    return true;
                }
            }
        }
        return super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.LEFT) {
            if (otherStack.getItem() instanceof ExpansionItem) {
                System.out.println("Expansion added");
                boolean added = addExpansion(stack, (ExpansionItem) otherStack.getItem());
                if (added) otherStack.decrement(1);
                return added;
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable(getTranslationKey()+".description").formatted(Formatting.GRAY));
        if (getExpansionSlots(stack) > 0){
            tooltip.add(Text.of(""));
            tooltip.add(Text.of("Expansions:"));
            List<ExpansionItem> expansions = this.getExpansions(stack);
            int nExpansions = expansions.toArray().length;
            for (int i = 0; i < getExpansionSlots(stack); i++){
                if (i < nExpansions){
                    tooltip.add(Text.translatable(expansions.get(i).getTranslationKey() + ".expansion"));
                }else {
                    tooltip.add(Text.translatable("item.arma_futuri.expansion.empty").formatted(Formatting.GRAY));

                }
                //tooltip.add(Text.of("⬤◯ Slot " + i));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);

    }

    public int getExpansionSlots(ItemStack stack){
        return getExpansionSlots(((ArmorItem)stack.getItem()).getType());
    }

    public abstract int getExpansionSlots(ArmorItem.Type type) ;

    //public abstract float getDefaultFuel();

    public float getMaxFuel(ItemStack stack){
        //float fuel = getDefaultFuel();
        float fuel = 0;
        for (ExpansionItem expansion : this.getExpansions(stack)) {
            fuel += expansion.addsFuel();
        }
        return fuel;
    }

    public static List<ExpansionItem> getExpansions(ItemStack stack) {
        int[] expansionIDs = stack.getNbt().getIntArray("Expansions");
        List<ExpansionItem> items = new ArrayList<>();
        for (int i = 0; i < expansionIDs.length; i++){
            //ArmaFuturiMod.LOGGER.info("Expansion ID: " + expansionIDs[i]);
            if (expansionIDs[i] != -1){
                //ArmaFuturiMod.LOGGER.info("Expansion Added With ID: " + expansionIDs[i]);
                items.add(ExpansionItems.get(expansionIDs[i]));
            }
        }
        return items;
    }

    public boolean addExpansion(ItemStack stack, ExpansionItem itemToAdd) {
        //ArmaFuturiMod.LOGGER.info("log1?");


        if ( getExpansions(stack).size() >= getExpansionSlots(((ArmorItem)stack.getItem()).getType()) || !itemToAdd.canBeEquippedOn(((ArmorItem) stack.getItem()).getSlotType())){
            //ArmaFuturiMod.LOGGER.info("Available Expansions: " + getExpansionSlots(((ArmorItem)stack.getItem()).getType()) + " Equipped Expansions: "  + len);
            return false;
        }
        List<ExpansionItem> expansions = this.getExpansions(stack);
        expansions.add(itemToAdd);

        //ArmaFuturiMod.LOGGER.info("hello?");
        setExpansions(stack, expansions);
        return true;
    }

    public void setExpansions(ItemStack stack, List<ExpansionItem> expansions) {
        int[] expansionIDs = new int[expansions.size()];

        for (int i = 0; i < expansionIDs.length; i++){
            expansionIDs[i] = expansions.get(i).getExpansionId();
        }
        stack.getNbt().putIntArray("Expansions", expansionIDs);
    }

    public ExpansionItem unequipExpansion(ItemStack stack) {
        /*
        int[] expansionIDs = stack.getNbt().getIntArray("Expansions");
        int[] newExpansionIds = new int[expansionIDs.length];
        System.arraycopy(expansionIDs, 0, newExpansionIds, 0, expansionIDs.length);
        ExpansionItem lastItem = null;
        int len = 0;
        for (int i = 0; i < expansionIDs.length; i++){
            if (expansionIDs[i] != -1) len++;
        }
        if (len > 0 && expansionIDs[len - 1] != -1){
            lastItem = ExpansionItems.get(expansionIDs[len - 1]);
            newExpansionIds[len - 1] = -1;
        }
        stack.getNbt().putIntArray("Expansions", newExpansionIds);
        */
        List<ExpansionItem> expansions = getExpansions(stack);
        ExpansionItem lastExpansion = null;
        if (expansions.size() > 0) {
            lastExpansion = expansions.get(expansions.size() - 1);
            expansions.remove(lastExpansion);
            setExpansions(stack, expansions);
        }
        return lastExpansion;
    }

    public static boolean hasExpansion(ItemStack stack, ExpansionItem item) {
        List<ExpansionItem> expansions = getExpansions(stack);
        return expansions.contains(item);
    }

    public static boolean hasExpansion(Entity entity, ExpansionItem item) {
        boolean hasItem = false;

       // if (entity instanceof PlayerEntity) {
        Iterable<ItemStack> armorItems = entity.getArmorItems();
        for (ItemStack itemStack : armorItems) {
            if (itemStack.getItem() instanceof ArmorItemWithExpansions) {
                hasItem = hasItem || ArmorItemWithExpansions.hasExpansion(itemStack, item);
            }
        }
        //}
        return hasItem;
    }
}
