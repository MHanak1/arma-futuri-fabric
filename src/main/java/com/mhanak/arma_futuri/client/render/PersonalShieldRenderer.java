package com.mhanak.arma_futuri.client.render;

import com.mhanak.arma_futuri.ArmaFuturiMod;

import com.mhanak.arma_futuri.entity.client.ModModelLayers;
import com.mhanak.arma_futuri.entity.client.PersonalShieldModel;
import com.mhanak.arma_futuri.entity.custom.PersonalShieldEntity;
import com.mhanak.arma_futuri.registry.ModRenderTypes;
import com.mhanak.arma_futuri.util.ILivingEntityAccess;
import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.fabric.event.FabricVeilRenderLevelStageEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class PersonalShieldRenderer extends LivingEntityRenderer<PersonalShieldEntity, PersonalShieldModel<PersonalShieldEntity>> {
    public PersonalShieldRenderer(EntityRendererFactory.Context context) {
        super(context, new PersonalShieldModel<>(context.getPart(ModModelLayers.PERSONAL_SHIELD)), 0.0f);
    }

    @Override
    public void render(PersonalShieldEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (VeilLevelPerspectiveRenderer.isRenderingPerspective()) {
            return;
        }
        Vec3d pos;
        if (((ILivingEntityAccess) livingEntity).getAttachmentEntity() != null) {
            pos = ((ILivingEntityAccess) livingEntity).getAttachmentEntity().getPos();
            livingEntity.setPosition(pos);
            livingEntity.setVelocity(((ILivingEntityAccess) livingEntity).getAttachmentEntity().getVelocity());
        }else {
            pos = livingEntity.getPos();
        }
        //build mesh, probably a better way to do this
        Matrix4f pose = matrixStack.peek().getPositionMatrix();
        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder builder = tesselator.getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        float s = 1.0f + livingEntity.getHealth()/livingEntity.getMaxHealth()/4;


        builder.vertex(-s, 0, s).next();
        builder.vertex( s, 0, s).next();
        builder.vertex( s, 2*s, s).next();
        builder.vertex(-s, 2*s, s).next();

        builder.vertex(-s, 2*s, -s).next();
        builder.vertex( s, 2*s, -s).next();
        builder.vertex( s, 0, -s).next();
        builder.vertex(-s, 0, -s).next();

        builder.vertex(-s, 0, -s).next();
        builder.vertex( s, 0, -s).next();
        builder.vertex( s, 0, s).next();
        builder.vertex(-s, 0, s).next();


        builder.vertex( s, 2*s, -s).next();
        builder.vertex(-s, 2*s, -s).next();
        builder.vertex(-s, 2*s, s).next();
        builder.vertex( s, 2*s, s).next();

        builder.vertex( s, 2*s, -s).next();
        builder.vertex( s, 2*s, s).next();
        builder.vertex( s, 0, s).next();
        builder.vertex( s, 0, -s).next();

        builder.vertex(-s, 0, -s).next();
        builder.vertex(-s, 0, s).next();
        builder.vertex(-s, 2*s, s).next();
        builder.vertex(-s, 2*s, -s).next();


        RenderSystem.setShaderColor(0.9F, 0.9F, 0.9F, 0.5F); //no idea what it does



        //setup the shader uniforms
        ShaderProgram shader = VeilRenderSystem.setShader(ArmaFuturiMod.path("shield"));

        if (shader == null) {
            ArmaFuturiMod.LOGGER.error("No Shield Shader Found");
            return;
        }
        shader.bind();

        shader.setVector( "spherePos", (float) pos.x, (float) pos.y, (float) pos.z);
        shader.setFloat("sphereSize", s);
        shader.setMatrix("modelViewMat", pose);


        //do the funee hit effect thing
        float opacity =  1.5F - (float) (livingEntity.maxHurtTime - livingEntity.hurtTime) /livingEntity.maxHurtTime*0.5f;

        opacity *= 1 - (float) (livingEntity.deathTime / 5.0);

        if (Float.isNaN(opacity)) {
            opacity = 1f;
        }

        shader.setFloat("opacity", opacity);

        //set the depth texture as a shader texture so the shader can access it
        RenderSystem.setShaderTexture(0, MinecraftClient.getInstance().getFramebuffer().getDepthAttachment());

        //actually draw the f-er
        ModRenderTypes.shield().draw(builder, RenderSystem.getVertexSorting());


        ShaderProgram.unbind();

        //as i said mystery fuction;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        //deffered light for G L O W  (totally not stolen from @idothehax)
        if ( VeilRenderSystem.renderer().getDeferredRenderer().isEnabled()){
            LightRenderer lightRenderer = VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer(); // Required


            if (livingEntity.getWorld().isClient()) { // Checks if it's on the client
                FabricVeilRenderLevelStageEvent.EVENT.register((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
                    if (stage == VeilRenderLevelStageEvent.Stage.AFTER_LEVEL) {
                        if (livingEntity.isAlive()) {

                            if (livingEntity.pointLight == null) { // If pointLight is not created yet
                                livingEntity.pointLight = new PointLight(); // Creates a new light
                                livingEntity.pointLight.setRadius(5.0f); // Sets how far it goes
                                livingEntity.pointLight.setBrightness(1.0f); // Sets how bright it can be
                                livingEntity.pointLight.setColor(0.6f, 0.6f, 1.0f);
                                lightRenderer.addLight(livingEntity.pointLight); // Spawns the light in
                            }
                            // Update the light's position based on the entity's position
                            livingEntity.pointLight.setPosition(livingEntity.getX(), livingEntity.getY() + 1.25f, livingEntity.getZ());

                        }else if (livingEntity.pointLight != null) { // If the helmet is removed or enchantment is not present, remove the light
                            lightRenderer.removeLight(livingEntity.pointLight);
                            livingEntity.pointLight = null;
                        }
                    }
                });
            }
        }
    }

    @Override
    public Identifier getTexture(PersonalShieldEntity entity) {
        return null;
    }
}
