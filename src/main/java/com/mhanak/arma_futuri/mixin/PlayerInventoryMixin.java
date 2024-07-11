package com.mhanak.arma_futuri.mixin;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
/*
    @Shadow @Final public DefaultedList<ItemStack> main;


    @ModifyArg(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(ILnet/minecraft/item/ItemStack;)Z"))
    private int insertStack(int slot, ItemStack stack) {
        return adjustForWeapons(slot, stack);
    }

    @ModifyArg(method = "addStack(Lnet/minecraft/item/ItemStack;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;addStack(ILnet/minecraft/item/ItemStack;)I"))
    private int addStack(int slot, ItemStack stack) {
        return adjustForWeapons(slot, stack);
    }

    //really hope this won't break anything important (foreshadowing)
    private int adjustForWeapons(int slot, ItemStack stack) {
        if (stack.getItem() instanceof WeaponItem){
            if (ArmorData.hasWeaponInHotbar((PlayerInventory)(Object)this) && ((PlayerInventory.isValidHotbarIndex(slot) || slot == -1))){
                for (int i = 0; i < main.size(); i++) {
                    if (!PlayerInventory.isValidHotbarIndex(i)){
                        if (main.get(i).isEmpty()){
                            return i;
                        }
                    }
                }
            }
        }
        return slot;
    }

 */
}
