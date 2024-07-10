package com.mhanak.arma_futuri.mixin.client;

import com.mhanak.arma_futuri.item.RifleItem;
import com.mhanak.arma_futuri.util.MinecraftClientAccess;
import foundry.veil.ext.EntityExtension;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.util.math.MathHelper.clamp;


@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements MinecraftClientAccess {
    @Unique
    private float xCameraVelocity = 0;
    @Unique
    private float yCameraVelocity = 0;


    @Override
    public void arma_futuri$addCameraVelocity(float x, float y) {
        this.xCameraVelocity += x;
        this.yCameraVelocity += y;
    }

    @Override
    public void arma_futuri$setCameraVelocity(float x, float y) {
        this.xCameraVelocity = x;
        this.yCameraVelocity = y;
    }

    @Override
    public Pair<Float, Float> arma_futuri$getCameraVelocity() {
        return Pair.of(this.xCameraVelocity, this.yCameraVelocity);
    }

    @Inject(at = @At("HEAD"), method = "handleBlockBreaking", cancellable = true)
    private void handleBlockBreaking(boolean breaking, CallbackInfo ci) {
        if (((MinecraftClient)(Object)this).player.getMainHandStack().getItem() instanceof RifleItem) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "render")
    private void render(boolean tick, CallbackInfo ci) {
        MinecraftClient client = ((MinecraftClient)(Object)this);
        if (client.player != null){
            client.player.changeLookDirection(xCameraVelocity * client.getLastFrameDuration(), yCameraVelocity * client.getLastFrameDuration());
            float cameraDampeningFactor = 2f;
            xCameraVelocity *= 1-clamp(cameraDampeningFactor *client.getLastFrameDuration(), 0, 1);
            yCameraVelocity *= 1-clamp(cameraDampeningFactor *client.getLastFrameDuration(), 0, 1);

            if (client.options.getPerspective().isFirstPerson()){
                EntityExtension extension = (EntityExtension) client.player;
                extension.veil$getEmitters().forEach(emitter -> {
                    if ("arma_futuri:jetpack_smoke".equals(String.valueOf(emitter.getRegistryName()))) {
                        emitter.remove();
                    }
                });
                extension.veil$getEmitters().removeIf(emitter -> "arma_futuri:jetpack_smoke".equals(String.valueOf(emitter.getRegistryName())));
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "doAttack", cancellable = true)
    public void doAttack(CallbackInfoReturnable<Boolean> cir) {
        if(MinecraftClient.getInstance().player != null) {
            if (MinecraftClient.getInstance().player.getMainHandStack().getItem() instanceof RifleItem) {
                //ClientPlayNetworking.send(ModPackets.SHOOT_ID, PacketByteBufs.create());
                cir.setReturnValue(false);
            }
        }
    }

}
