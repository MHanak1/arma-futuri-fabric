package com.mhanak.arma_futuri.mixin.client;

import com.mhanak.arma_futuri.item.WeaponItem;
import com.mhanak.arma_futuri.util.IEntityAccess;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Inject(at = @At("HEAD"), method = "renderFirstPersonItem", cancellable = true)
    public void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        if (item.getItem() instanceof WeaponItem){
            if (((IEntityAccess)player).isAiming()){
                if (((WeaponItem)item.getItem()).hasScope()) ci.cancel();
                matrices.translate(-0.375D, 0.17D, 0.2D);
            }
        }
    }
}
