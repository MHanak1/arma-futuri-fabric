package com.mhanak.arma_futuri.mixin.client;

import com.mhanak.arma_futuri.item.RifleItem;
import com.mhanak.arma_futuri.util.IEntityAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Mouse.class)
public class MouseMixin {

    //this took me wayyy to long to do
    @ModifyVariable(method = "updateMouse", at = @At("STORE"), ordinal = 4)
    private double injected(double value){
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            ItemStack stack = player.getMainHandStack();
            if (stack.getItem() instanceof RifleItem && ((IEntityAccess)player).isAiming()){
                return value*((RifleItem) stack.getItem()).getFovMultiplier();
            }
        }
        return value;
    }
}
