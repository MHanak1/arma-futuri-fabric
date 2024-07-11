package com.mhanak.arma_futuri.mixin;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Slot.class)
public abstract class SlotMixin {
    /*
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

     */
}
