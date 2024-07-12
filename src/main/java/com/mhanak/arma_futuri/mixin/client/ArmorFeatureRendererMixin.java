package com.mhanak.arma_futuri.mixin.client;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.client.render.JetpackModel;
import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import com.mhanak.arma_futuri.registry.ExpansionItems;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {
    private static final Identifier JETPACK_TEXTURE = ArmaFuturiMod.path("textures/entity/jetpack.png");
    private SoundInstance jetpackSound;

    @Inject(method = "renderArmor", at = @At(value = "HEAD"))
    private void renderArmor (MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot, int light, BipedEntityModel model, CallbackInfo ci) {

        if (entity.getWorld().isClient() /*&& entity instanceof PlayerEntity*/ && !entity.isSpectator()) { // Checks if it's on the client
            if (armorSlot == EquipmentSlot.HEAD){

            }if (armorSlot == EquipmentSlot.CHEST) {
                if (ArmorItemWithExpansions.hasExpansion(entity, ExpansionItems.JETPACK)){
                    ModelPart notYetJetpackModel = JetpackModel.getTexturedModelData().createModel();
                    JetpackModel jetpackModel = new JetpackModel(notYetJetpackModel);
                    jetpackModel.setTransform(model.body.getTransform());
                    renderJetpack(matrices, vertexConsumers, light, jetpackModel, 1, 1, 1);
                }
            }
        }
    }

    private void renderJetpack(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, JetpackModel model, float red, float green, float blue) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(JETPACK_TEXTURE));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0f);
    }
}
