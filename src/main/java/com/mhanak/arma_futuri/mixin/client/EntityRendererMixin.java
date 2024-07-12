package com.mhanak.arma_futuri.mixin.client;

import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import com.mhanak.arma_futuri.registry.ExpansionItems;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.quasar.particle.ParticleEmitter;
import foundry.veil.api.quasar.particle.ParticleSystemManager;
import foundry.veil.ext.EntityExtension;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "render", at = @At("HEAD"))
    public void render(T entity, float pEntityYaw, float pPartialTick, MatrixStack pPoseStack, VertexConsumerProvider pBuffer, int pPackedLight, CallbackInfo ci) {
        if (ArmorItemWithExpansions.hasExpansion(entity, ExpansionItems.JETPACK)) {
            boolean flying = false;

            if (entity instanceof PlayerEntity) {
                if (((PlayerEntity) entity).getAbilities().flying) {
                    flying = true;
                }
            }

            EntityExtension extension = (EntityExtension) entity;
            boolean shouldMakeParticles =
                !entity.isOnGround() &&
                !entity.isSwimming() &&
                !entity.isSpectator() &&
                !flying &&
                ArmorData.getJetpackFuel((IEntityAccess) entity) > 0 &&
                (
                        ArmorData.getJetpackActive((IEntityAccess) entity) ||
                                entity.isSneaking()
                );
            if (shouldMakeParticles) {
                boolean alreadyHasParticles = false;
                for (ParticleEmitter emmiter : extension.veil$getEmitters()) {
                    //ArmaFuturiMod.LOGGER.info("{}, {}", emmiter.getRegistryName(), "arma_futuri:jetpack_smoke");
                    if (String.valueOf(emmiter.getRegistryName()).equals("arma_futuri:jetpack_smoke")) {

                        alreadyHasParticles = true;
                        break;
                    }
                }
                if (!alreadyHasParticles) {
                    ParticleSystemManager particleManager = VeilRenderSystem.renderer().getParticleManager();
                    ParticleEmitter instance = particleManager.createEmitter(new Identifier("arma_futuri:jetpack_smoke"));
                    if (instance == null) {
                        return;
                    }

                    instance.setPosition(0, entity.getBodyY(0.5) - entity.getY(), 0);

                    instance.setAttachedEntity(entity);

                    extension.veil$addEmitter(instance);
                    particleManager.addParticleSystem(instance);
                } else {
                    extension.veil$getEmitters().forEach(emitter -> {
                        if ("arma_futuri:jetpack_smoke".equals(String.valueOf(emitter.getRegistryName())) && entity instanceof LivingEntity) {
                            double xoffset = Math.sin(((LivingEntity) entity).bodyYaw / 180 * Math.PI);
                            double zoffset = -Math.cos(((LivingEntity) entity).bodyYaw / 180 * Math.PI);
                            float mul = 0.3f;
                            float vOffset = 0f;
                            if (entity.isSneaking()) {
                                mul = 0.6f;
                                vOffset = -0.5f;
                            }

                            emitter.setPosition(xoffset * mul, entity.getBodyY(0.5) - entity.getY() + vOffset, zoffset * mul);
                        }
                    });
                }
            } else {
                //            ((EntityExtension) pEntity).getEmitters().stream().filter(emitter -> emitter.registryName.toString().equals("veil:basic_smoke")).forEach(p -> p.getEmitterModule().setLoop(false));
                extension.veil$getEmitters().forEach(emitter -> {
                    if ("arma_futuri:jetpack_smoke".equals(String.valueOf(emitter.getRegistryName()))) {
                        emitter.remove();
                    }
                });
                extension.veil$getEmitters().removeIf(emitter -> "arma_futuri:jetpack_smoke".equals(String.valueOf(emitter.getRegistryName())));
            }
        }
    }
}
