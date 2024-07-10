package com.mhanak.arma_futuri.item;

import com.mhanak.arma_futuri.item.expansion.ExpansionItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ExpansionItem extends Item {
    public ExpansionItem(Settings settings) {
        super(settings.maxCount(1));
    }

    public int getExpansionId() {
        return ExpansionItems.getExtensionId(this);
    }

    abstract public EquipmentSlot getEquipableOn();

    public boolean canBeEquippedOn(EquipmentSlot slot) {
        return slot == getEquipableOn();
    }

    public float addsFuel() {
        return 0;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable(getTranslationKey() + ".description").formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
