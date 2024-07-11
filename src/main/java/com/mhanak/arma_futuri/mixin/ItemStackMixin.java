package com.mhanak.arma_futuri.mixin;

import com.mhanak.arma_futuri.item.WeaponItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "canCombine", at =  @At("HEAD"), cancellable = true)
    private static void canCombine(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getHolder() instanceof PlayerEntity) {
            PlayerInventory inventory = ((PlayerEntity) stack.getHolder()).getInventory();
            if (stack.getItem() instanceof WeaponItem) {
                if (PlayerInventory.isValidHotbarIndex(inventory.indexOf(stack))) {
                    for (int i = 0; i < inventory.size(); i++) {
                        if (PlayerInventory.isValidHotbarIndex(i)) {
                            if (inventory.getStack(i).getItem() instanceof WeaponItem) {
                                cir.setReturnValue(false);
                            }
                        }
                    }
                }
            }
        }
    }
}
