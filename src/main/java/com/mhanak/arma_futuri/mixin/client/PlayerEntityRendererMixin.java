package com.mhanak.arma_futuri.mixin.client;

import com.mhanak.arma_futuri.item.WeaponItem;
import com.mhanak.arma_futuri.util.IEntityAccess;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(at = @At("HEAD"), method = "getArmPose", cancellable = true)
    private static void getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack stack = player.getStackInHand(hand);
        if (hand == Hand.MAIN_HAND) {
            if (stack.getItem() instanceof WeaponItem){
                if (((IEntityAccess)player).isAiming()){
                    cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
                }else {
                    cir.setReturnValue(BipedEntityModel.ArmPose.ITEM);
                }
            }
        }
    }
}
