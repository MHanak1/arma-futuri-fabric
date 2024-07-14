package com.mhanak.arma_futuri.mixin;

import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import com.mhanak.arma_futuri.registry.ExpansionItems;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow private boolean floating;

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci) {
        PlayerEntity player = ((ServerPlayNetworkHandler) (Object) this).player;
        float fuel = ArmorData.getJetpackFuel((IEntityAccess) player);

        if (ArmorItemWithExpansions.hasExpansion(player, ExpansionItems.JETPACK) && fuel > 0 && ArmorData.getJetpackActive((IEntityAccess) player)) {
            this.floating = false;
        }
    }
}
