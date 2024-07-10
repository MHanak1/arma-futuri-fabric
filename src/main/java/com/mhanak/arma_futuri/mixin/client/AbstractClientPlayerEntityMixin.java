package com.mhanak.arma_futuri.mixin.client;

import com.mhanak.arma_futuri.item.RifleItem;
import com.mhanak.arma_futuri.util.IEntityAccess;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Inject(at = @At("HEAD"), method = "getFovMultiplier", cancellable = true)
    private void getFovMultiplier(CallbackInfoReturnable<Float> cir) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof RifleItem){
            if (((IEntityAccess)player).isAiming()){
                cir.setReturnValue(((RifleItem) stack.getItem()).getFovMultiplier());
            }
        }
    }
}
