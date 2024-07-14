package com.mhanak.arma_futuri.entity.client.armor.RenderLayer;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.entity.client.armor.HeavyCombatArmorRenderer;
import com.mhanak.arma_futuri.item.armor.HeavyCombatArmorItem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class HeavyCombatArmorColorLayer extends GeoRenderLayer<HeavyCombatArmorItem> {
    private static final Identifier TEXTURE = ArmaFuturiMod.path("textures/item/armor/heavy_combat_armor_overlay.png");
    private HeavyCombatArmorRenderer sneakyArmorRenderer;

    public HeavyCombatArmorColorLayer(HeavyCombatArmorRenderer armorRenderer) {
        super(armorRenderer);
        sneakyArmorRenderer = armorRenderer;
    }

    // Apply the overlay texture witht the right color
    @Override
    public void render(MatrixStack poseStack, HeavyCombatArmorItem animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderLayer armorRenderType = RenderLayer.getArmorCutoutNoCull(TEXTURE);

        int colorInt = sneakyArmorRenderer.getColor() | 0xFF000000;
        final float saturation = 0.8f;

        float[] col = new float[3];

        col[0] = (float)(colorInt >> 16 & 0xFF) / 255.0f;
        col[1] = (float)(colorInt >> 8 & 0xFF) / 255.0f;
        col[2] = (float)(colorInt & 0xFF) / 255.0f;

        for (int i = 0; i < col.length; i++){
            col[i] *= saturation;
            col[i] += 1-saturation;
            col[i] = col[i] * col[i];
        }

        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType,
                bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
                col[0], col[1], col[2], 1);
    }
}
