package com.mhanak.arma_futuri.registry;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import foundry.veil.api.client.render.VeilRenderBridge;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;


public final class ModRenderTypes extends RenderLayer {

    private static final ShaderProgram MIRROR_SHADER = VeilRenderBridge.shaderState(ArmaFuturiMod.path("mirror"));
    private static final ShaderProgram SHIELD_SHADER = VeilRenderBridge.shaderState(ArmaFuturiMod.path("shield"));


    private static final RenderLayer MIRROR = of(
            "mirror",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            VertexFormat.DrawMode.QUADS,
            DEFAULT_BUFFER_SIZE,
            true,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(MIRROR_SHADER)
                    .layering(POLYGON_OFFSET_LAYERING)
                    .build(true));

    private static final RenderLayer SHIELD = of(
        "shield",
            VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS,
            DEFAULT_BUFFER_SIZE,
            true,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(SHIELD_SHADER)
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    //.overlay(Overlay.ENABLE_OVERLAY_COLOR)
                    //.cull(Cull.DISABLE_CULLING)
                    .transparency(Transparency.TRANSLUCENT_TRANSPARENCY)
                    .target(Target.TRANSLUCENT_TARGET)
                    .build(true)
    );


    private ModRenderTypes(String string, VertexFormat vertexFormat, VertexFormat.DrawMode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }

    public static RenderLayer mirror() {
        return MIRROR;
    }

    public static RenderLayer shield() {
        return SHIELD;
    }
}
