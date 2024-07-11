package com.mhanak.arma_futuri.mixin;

import com.mhanak.arma_futuri.item.WeaponItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow @Final public Inventory inventory;

    @Shadow @Final private int index;

    @Shadow public abstract boolean hasStack();

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void canTakeItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (inventory instanceof PlayerInventory && stack.getItem() instanceof WeaponItem) {
            if (PlayerInventory.isValidHotbarIndex(this.index)) {
                for (int i = 0; i < this.inventory.size(); i++) {
                    if (PlayerInventory.isValidHotbarIndex(i)) {
                        if (this.inventory.getStack(i).getItem() instanceof WeaponItem) {
                            cir.setReturnValue(false);
                        }
                    }
                }
            }
        }
    }
}
