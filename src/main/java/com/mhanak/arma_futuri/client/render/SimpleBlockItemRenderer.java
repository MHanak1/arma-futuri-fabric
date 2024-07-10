package com.mhanak.arma_futuri.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class SimpleBlockItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    private final BlockEntity be;

    public SimpleBlockItemRenderer(BlockEntity be) {
        this.be = be;
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack poseStack, VertexConsumerProvider source, int light, int overlay) {
        MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(this.be, poseStack, source, light, overlay);
    }
}
