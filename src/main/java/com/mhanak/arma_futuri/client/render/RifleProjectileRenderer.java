package com.mhanak.arma_futuri.client.render;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.entity.client.ModModelLayers;
import com.mhanak.arma_futuri.entity.client.PersonalShieldModel;
import com.mhanak.arma_futuri.entity.client.RifleProjectileEntityModel;
import com.mhanak.arma_futuri.entity.projectile.RifleProjectileEntity;
import foundry.veil.VeilClient;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import foundry.veil.api.client.render.deferred.light.PointLight;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.DragonFireballEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RifleProjectileRenderer extends EntityRenderer<RifleProjectileEntity> {

    private static final Identifier TEXTURE = ArmaFuturiMod.path("textures/entity/projectile/rifle_projectile.png");

    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    private static final float size = 0.5f;
    public RifleProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(RifleProjectileEntity rifleProjectileEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        //if (VeilDeferredRenderer.isSupported()){
        LightRenderer lightRenderer = VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer(); // Required

        if (rifleProjectileEntity.getPointLight() == null && !rifleProjectileEntity.landed) {
            rifleProjectileEntity.setPointLight(new PointLight());

            rifleProjectileEntity.getPointLight().setColor(new Vector3f(0.4f, 0.4f, 1f));

            rifleProjectileEntity.getPointLight().setBrightness(1f);

            lightRenderer.addLight(rifleProjectileEntity.getPointLight());
        }else if (rifleProjectileEntity.pointLight != null){
            Vec3d pos = rifleProjectileEntity.getLerpedPos(tickDelta);

            float radius = 1f;
            if (rifleProjectileEntity.landed) {
                radius = 4 - rifleProjectileEntity.timeLanded - tickDelta;
            }

            if (radius <= 0){
                lightRenderer.removeLight(rifleProjectileEntity.getPointLight());
            }

            rifleProjectileEntity.getPointLight().setRadius(radius);
            rifleProjectileEntity.getPointLight().setPosition(pos.x, pos.y, pos.z);


            matrixStack.push();
            matrixStack.scale(1.0f, 1.0f, 1.0f);
            matrixStack.multiply(this.dispatcher.getRotation());
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            MatrixStack.Entry entry = matrixStack.peek();
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);



            RifleProjectileRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0, 0, 0, 1);
            RifleProjectileRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, i, size, 0, 1, 1);
            RifleProjectileRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, i, size, size, 1, 0);
            RifleProjectileRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0, size, 0, 0);
//            RifleProjectileEntityModel.getTexturedModelData().createModel().render(matrixStack, vertexConsumerProvider.getBuffer(LAYER), 15, 0);

            matrixStack.pop();
        }

        super.render(rifleProjectileEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, i);
    }

    private static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, float y, int textureU, int textureV) {
        vertexConsumer.vertex(positionMatrix, x - size/2, y - size/2, 0.0f).color(255, 255, 255, 255).texture(textureU, textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }


    @Override
    public Identifier getTexture(RifleProjectileEntity entity) {
        return TEXTURE;
    }
}
