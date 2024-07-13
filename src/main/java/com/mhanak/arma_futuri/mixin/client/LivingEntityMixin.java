package com.mhanak.arma_futuri.mixin.client;

import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import com.mhanak.arma_futuri.item.ExpansionItem;
import com.mhanak.arma_futuri.registry.ExpansionItems;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import com.mhanak.arma_futuri.util.ILivingEntityAccess;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.fabric.event.FabricVeilRenderLevelStageEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;


@Mixin(LivingEntity.class)

public abstract class LivingEntityMixin implements ILivingEntityAccess {
    @Shadow public abstract boolean isAlive();

    @Shadow public abstract boolean hasNoDrag();

    @Shadow protected abstract int getNextAirOnLand(int air);

    @Shadow public abstract void remove(Entity.RemovalReason reason);

    private static TrackedData<OptionalInt> attachedEntity;
    private static TrackedData<OptionalInt> attachmentEntity;

    private boolean flashlightIsSetUp = false;

    @Unique
    private void addFlashLight(){
        if (!(((Entity) (Object) this) instanceof PlayerEntity)) return;
        FabricVeilRenderLevelStageEvent.EVENT.register((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            PlayerEntity entity = (PlayerEntity) (Object) this;
            LightRenderer lightRenderer = VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer();

            ItemStack item = entity.getEquippedStack(EquipmentSlot.HEAD);
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_LEVEL) {
                //ArmaFuturiMod.LOGGER.info("Equipped armor: {}, expected: {}", item.getItem(), ModItems.LIGHT_COMBAT_HELMET);
                if (item.getItem() instanceof ArmorItemWithExpansions && ArmorData.getFlashlightActive((IEntityAccess) entity) && entity.isAlive()) {
                    if (((IEntityAccess)entity).arma_futuri$getAreaLight() == null) { // If ((IEntityDataSaver)entity).pointLight is not created yet
                        ((IEntityAccess)entity).arma_futuri$setAreaLight(new AreaLight()); // Creates a new light
                        if (ArmorItemWithExpansions.hasExpansion(entity, (ExpansionItem) ExpansionItems.FLASHLIGHT_UPGRADE)) {
                            ((IEntityAccess) entity).arma_futuri$getAreaLight().setDistance(60.0f); // Sets how far it goes
                        } else {
                            ((IEntityAccess) entity).arma_futuri$getAreaLight().setDistance(30.0f); // Sets how far it goes
                        }
                        ((IEntityAccess)entity).arma_futuri$getAreaLight().setBrightness(1.5f); // Sets how bright it can be
                        ((IEntityAccess)entity).arma_futuri$getAreaLight().setSize(0.03, 0.03);


                        lightRenderer.addLight(((IEntityAccess)entity).arma_futuri$getAreaLight()); // Spawns the light in
                        //ArmaFuturiMod.LOGGER.info("Light added at position: {}", ((IEntityDataSaver) entity).getAreaLight().getPosition().toString());
                    }
                    // Update the light's position and rotation
                    Vec3d pos = entity.getCameraPosVec(0.0f);
                    ((IEntityAccess)entity).arma_futuri$getAreaLight().setPosition(pos.x, pos.y, pos.z);
                    Vector3f lookdir = entity.getRotationVector().toVector3f().mul(-1);
                    //Vector3f lookdir = model.getHead().getTransform().
                    ((IEntityAccess)entity).arma_futuri$getAreaLight().setOrientation(new Quaternionf().lookAlong(lookdir, new Vector3f(0, 1, 0)));

                    //ArmaFuturiMod.LOGGER.info("Light updated to position: {}", ((IEntityDataSaver) entity).getAreaLight().getPosition().toString());
                } else {
                    removeFlashlight();
                }
            }
        });
    }

    @Unique
    private void removeFlashlight() {
        if (((IEntityAccess) this).arma_futuri$getAreaLight() != null && ((Entity)(Object)this) instanceof PlayerEntity) {
            PlayerEntity entity = (PlayerEntity) (Object) this;
            LightRenderer lightRenderer = VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer();
            lightRenderer.removeLight(((IEntityAccess) entity).arma_futuri$getAreaLight());
            ((IEntityAccess) entity).arma_futuri$setAreaLight(null);
        }
        //ArmaFuturiMod.LOGGER.info("Light removed");
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (flashlightIsSetUp) return;
        addFlashLight();
        flashlightIsSetUp = true;
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void remove(Entity.RemovalReason reason, CallbackInfo ci) {
        if (((LivingEntity)(Object)this).getWorld().isClient) {
            removeFlashlight();
        }
    }
}

    //"getEntityCutoutNoCull(net/minecraft/util/Identifier; net)net/minecraft/client/render/RenderLayer;(Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/render/RenderType;"
//
//
//    @Inject(method = "getEntityCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;", at = @At("HEAD"), cancellable = true)
//    private static void replaceEntity(Identifier texture, CallbackInfoReturnable<RenderLayer> cir) {
//        cir.setReturnValue(VeilExampleRenderTypes.cursedTessellationRenderTypeEntityCutoutNoCullWhyIsThisInAFundyVideoIBetHeWillProbablySayHeIsTheOneWhoCodedItInMinecraft(resourceLocation, bl));
//    }
//}
