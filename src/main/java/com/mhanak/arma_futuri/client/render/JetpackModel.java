// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.mhanak.arma_futuri.client.render;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class JetpackModel extends EntityModel<Entity> {
	private final ModelPart jetpack;
	public JetpackModel(ModelPart root) {
		this.jetpack = root.getChild("jetpack");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData jetpack = modelPartData.addChild("jetpack", ModelPartBuilder.create().uv(1, 11).cuboid(-2.0F, 1.0F, 4.0F, 4.0F, 8.0F, 3.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-4.0F, 2.0F, 3.0F, 8.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData cube_r1 = jetpack.addChild("cube_r1", ModelPartBuilder.create().uv(16, 10).cuboid(-2.0F, -1.0F, -1.0F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F))
				.uv(25, 16).cuboid(-2.0F, 5.0F, -1.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 5.0F, 4.0F, 0.4363F, 0.5236F, 0.0F));

		ModelPartData cube_r2 = jetpack.addChild("cube_r2", ModelPartBuilder.create().uv(0, 22).cuboid(-1.0F, 5.0F, -1.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
				.uv(20, 0).cuboid(-1.0F, -1.0F, -1.0F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 5.0F, 4.0F, 0.4363F, -0.5236F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		jetpack.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	public void setTransform (ModelTransform transform) {
		jetpack.setTransform(transform);
	}
}