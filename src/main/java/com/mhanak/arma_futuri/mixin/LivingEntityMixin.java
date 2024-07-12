package com.mhanak.arma_futuri.mixin;

import com.mhanak.arma_futuri.entity.custom.PersonalShieldEntity;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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


    public Entity getAttachedEntity() {

        OptionalInt id = ((Entity)(Object)this).getDataTracker().get(attachedEntity);
        if (id.isPresent()) {
            return ((Entity)(Object)this).getWorld().getEntityById(id.getAsInt());
        }
        return null;
    }

    public void setAttachedEntity(Entity entityToAttach) {
        if (entityToAttach == null) {
            ((Entity)(Object)this).getDataTracker().set (attachedEntity, OptionalInt.empty());
        }
        else {
            ((Entity) (Object) this).getDataTracker().set(attachedEntity, OptionalInt.of(entityToAttach.getId()));
        }
    }

    public Entity getAttachmentEntity() {
        OptionalInt id = ((Entity)(Object)this).getDataTracker().get(attachmentEntity);
        if (id.isPresent()) {
            return  ((Entity)(Object)this).getWorld().getEntityById(id.getAsInt());
        }
        return null;
    }

    public void setAttachmentEntity(Entity entityToBeAttachedTo) {
        if (entityToBeAttachedTo == null) {
            ((Entity)(Object)this).getDataTracker().set(attachmentEntity, OptionalInt.empty());
        }
        else{
        ((Entity)(Object)this).getDataTracker().set(attachmentEntity, OptionalInt.of(entityToBeAttachedTo.getId()));
        }
    }

    public void attachToEntity(Entity entity) {

        this.setAttachmentEntity(entity);
        ((ILivingEntityAccess) entity).setAttachedEntity((Entity) (Object) this);
/*
        if (!player.getWorld().isClient){
            ArmaFuturiMod.LOGGER.info("Attaching entity " + ((Entity) (Object)this).getId() + " to " + player.getUuid());

            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeUuid(player.getUuid());
            buffer.writeInt(((Entity) (Object)this).getId());

            for (ServerPlayerEntity otherPlayer : ((LivingEntity)(Object) this).getServer().getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(otherPlayer, ModPackets.ATTACH_ID, buffer);
            }
            ArmaFuturiMod.LOGGER.info("Sent packet");
        }
        */
    }

    public void detachFromEntity() {
        ((ILivingEntityAccess)(((ILivingEntityAccess)this).getAttachmentEntity())).setAttachedEntity(null);
        ((ILivingEntityAccess)this).setAttachmentEntity(null);
    }

    @Unique
    @Environment(EnvType.CLIENT)
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
    @Environment(EnvType.CLIENT)
    private void removeFlashlight() {
        if (((IEntityAccess) this).arma_futuri$getAreaLight() != null && ((Entity)(Object)this) instanceof PlayerEntity) {
            PlayerEntity entity = (PlayerEntity) (Object) this;
            LightRenderer lightRenderer = VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer();
            lightRenderer.removeLight(((IEntityAccess) entity).arma_futuri$getAreaLight());
            ((IEntityAccess) entity).arma_futuri$setAreaLight(null);
        }
        //ArmaFuturiMod.LOGGER.info("Light removed");
    }

    @ModifyVariable(method = "handleFallDamage", at = @At("HEAD"), ordinal = 1)
    private float damageMultiplier(float multiplier) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity) {
            return multiplier * (ArmorData.getFallDamageMultiplier((PlayerEntity) entity));
        }
        return multiplier;
    }

    @Inject(method = "getNextAirUnderwater", at = @At("HEAD"), cancellable = true)
    private void getNextAirUnderwater(int air, CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        for (ItemStack stack : entity.getArmorItems()){
            if (stack.getItem() instanceof ArmorItemWithExpansions && entity instanceof PlayerEntity){
                if (((ArmorItemWithExpansions) stack.getItem()).getType() == ArmorItem.Type.HELMET){
                    if (ArmorItemWithExpansions.hasExpansion(entity, ExpansionItems.OXYGEN_RECYCLER_UPGRADE)){
                        cir.setReturnValue(getNextAirOnLand(air));
                        return;
                    }
                    float rand = entity.getRandom().nextFloat();
                    if (rand > ArmorData.getOxygenUsageMultiplier((PlayerEntity) entity)){
                        cir.setReturnValue(air);
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (flashlightIsSetUp || !((LivingEntity)(Object)this).getWorld().isClient ) return;
        addFlashLight();
        flashlightIsSetUp = true;
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void remove(Entity.RemovalReason reason, CallbackInfo ci) {
            removeFlashlight();
    }

    @Inject(at = @At("HEAD"), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        boolean hasShield = ((ILivingEntityAccess)this).getAttachedEntity() instanceof PersonalShieldEntity;
        if(hasShield && (
            source.isIn(DamageTypeTags.IS_EXPLOSION) ||
            source.isIn(DamageTypeTags.IS_LIGHTNING) ||
            source.isIn(DamageTypeTags.IS_PROJECTILE)
        )){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "initDataTracker", at = @At("HEAD"))
    private void initDataTracker(CallbackInfo ci) {
        ((LivingEntity)(Object)this).getDataTracker().startTracking(attachedEntity, OptionalInt.empty());
        ((LivingEntity)(Object)this).getDataTracker().startTracking(attachmentEntity, OptionalInt.empty());
    }

    static {
        attachedEntity = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.OPTIONAL_INT);
        attachmentEntity = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.OPTIONAL_INT);
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
