package com.mhanak.arma_futuri;

import com.mhanak.arma_futuri.client.render.MirrorBlockEntityRenderer;
import com.mhanak.arma_futuri.client.render.PersonalShieldRenderer;
import com.mhanak.arma_futuri.client.render.RifleProjectileRenderer;
import com.mhanak.arma_futuri.entity.client.ModModelLayers;
import com.mhanak.arma_futuri.entity.client.PersonalShieldModel;
import com.mhanak.arma_futuri.entity.client.RifleProjectileEntityModel;
import com.mhanak.arma_futuri.event.KeyInputHandler;
import com.mhanak.arma_futuri.networking.ModPackets;
import com.mhanak.arma_futuri.registry.ModBlocks;
import com.mhanak.arma_futuri.registry.ModEntities;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.fabric.event.FabricVeilRenderLevelStageEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ArmaFuturiModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        KeyInputHandler.register();
        ModPackets.registerC2SPackets();

        EntityRendererRegistry.register(ModEntities.PERSONAL_SHIELD, PersonalShieldRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.PERSONAL_SHIELD, PersonalShieldModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.RIFLE_PROJECTILE, RifleProjectileRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.RIFLE_PROJECTILE, RifleProjectileEntityModel::getTexturedModelData);

        //BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.MAP, new SimpleBlockItemRenderer(new MapBlockEntity(BlockPos.ORIGIN, ModBlocks.MAP.getDefaultState())));
       // BlockEntityRendererFactories.register(ModBlocks.MAP_BE, MapBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.MIRROR_BE, MirrorBlockEntityRenderer::new);
        //FabricVeilRendererEvent.EVENT.register(renderer -> renderer.getEditorManager().add(new VeilExampleModEditor()));

        FabricVeilRenderLevelStageEvent.EVENT.register((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_LEVEL) {
                MirrorBlockEntityRenderer.renderLevel(MinecraftClient.getInstance().world, projectionMatrix, partialTicks, VeilRenderBridge.create(frustum), camera);
            }
        });

        //HudRenderCallback.EVENT.register(new ShieldHudOverlay());
    }
}