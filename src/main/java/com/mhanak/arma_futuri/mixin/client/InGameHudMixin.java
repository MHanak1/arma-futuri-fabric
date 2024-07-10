package com.mhanak.arma_futuri.mixin.client;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import com.mhanak.arma_futuri.item.RifleItem;
import com.mhanak.arma_futuri.item.expansion.ExpansionItems;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private static final Identifier FULL_TEXTURE = ArmaFuturiMod.path("textures/gui/shield_overlay_full.png");
    @Unique
    private static final Identifier HALF_TEXTURE = ArmaFuturiMod.path("textures/gui/shield_overlay_half.png");
    @Unique
    private static final Identifier SHIELD_OVERLAY_TEXTURE = new Identifier("minecraft", "textures/misc/underwater.png");



    @Inject(at= @At("HEAD"), method = "renderCrosshair", cancellable = true)
    private void renderCrosshair(DrawContext context, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null){
            if (MinecraftClient.getInstance().player.getMainHandStack().getItem() instanceof RifleItem){
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "render")
    private void render(DrawContext drawContext, float tickDelta, CallbackInfo ci) {
        doMichaelsHud(drawContext, tickDelta);
    }

    private void doMichaelsHud(DrawContext drawContext, float tickDelta) {
        int x = 0;
        int y = 0;
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null) return;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        x = width / 2;
        y = height;

        int xoffset = 0;

        if (!player.getOffHandStack().isEmpty()) {
            xoffset += 29;
        }

        if(!player.isSpectator()){
            boolean hasShield = ArmorItemWithExpansions.hasExpansion(player, ExpansionItems.SHIELD_MODULE);
            boolean hasJetpack = ArmorItemWithExpansions.hasExpansion(player, ExpansionItems.JETPACK);
            if (hasShield && !player.isCreative()){
                drawShieldHealth(x - 91, y - 59, drawContext);
                if (ArmorData.getShieldActive((IEntityAccess) player)){
                    renderShieldOverlay(client, drawContext.getMatrices());
                }
            }
            if (hasJetpack){
                drawJetpackFuel(x - 104 - xoffset, y-3, drawContext);
                xoffset += 13;
            }
            if (player.getMainHandStack().getItem() instanceof RifleItem){
                drawWeaponEnergy(x - 104 - xoffset, y-3, drawContext);
                xoffset += 13;
            }
            Item item = player.getMainHandStack().getItem();
            if (item instanceof RifleItem){
                if (((RifleItem) item).hasScope() && ((IEntityAccess)player).isAiming() && client.options.getPerspective().isFirstPerson()) {
                    RenderSystem.enableBlend();
                    renderScopeOverlay(drawContext, 1.0f, ((RifleItem) item).getScopeTexture());
                }
            }
        }
    }

    @Unique
    private static void drawJetpackFuel(int x, int y, DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        float fuel = ArmorData.getJetpackFuel((IEntityAccess) player);
        float maxFuel = ArmorData.getMaxFuel(player);

        drawBar(drawContext, x, y, fuel/maxFuel, 0xFFffa500);

        //drawContext.fill(0, 0, 100, 100, 0, 0xFF000000);
    }

    @Unique
    private static void drawWeaponEnergy(int x, int y, DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        float fuel = ArmorData.getWeaponEnergy((IEntityAccess) player);
        float maxFuel = ArmorData.getMaxWeaponEnergy((IEntityAccess) player);

        drawBar(drawContext, x, y, fuel/maxFuel, 0xFF3333FF);

        //drawContext.fill(0, 0, 100, 100, 0, 0xFF000000);
    }


    private static void drawBar(DrawContext drawContext, int x, int y, float fillPercentage, int color){
        drawContext.fill(x, y, x+9, y-40, 0, 0xFF222222);
        drawContext.fill(x, y - (int) (40*fillPercentage) , x+9, y,0, color);
        drawContext.drawBorder(x, y-40, 9, 40, 0xFF000000);
    }

    @Unique
    private static void drawShieldHealth(int x, int y, DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, FULL_TEXTURE);
        float health = ArmorData.getShieldHealth(player);
        for (int i = 0; i < 10; i++){
            if (i < Math.floor(health / 3)){
                drawContext.drawTexture(FULL_TEXTURE, x + (i * 8), y, 0, 0, 9, 9, 9, 9);
            }else if ((ArmorData.getShieldHealth(player)/3) % 1 > 0.5) {
                drawContext.drawTexture(HALF_TEXTURE, x + (i * 8), y, 0, 0, 9, 9, 9, 9);
                break;
            }

        }
    }

    @Unique
    private void renderScopeOverlay(DrawContext context, float scale, Identifier texture) {
        int scaledWidth = context.getScaledWindowWidth();
        int scaledHeight = context.getScaledWindowHeight();
        float f;
        float g = f = (float)Math.min(scaledWidth, scaledHeight);
        float h = Math.min((float)scaledWidth / f, (float)scaledHeight / g) * scale;
        int i = MathHelper.floor(f * h);
        int j = MathHelper.floor(g * h);
        int k = (scaledWidth - i) / 2;
        int l = (scaledHeight - j) / 2;
        int m = k + i;
        int n = l + j;
        context.drawTexture(texture, k, l, -90, 0.0f, 0.0f, i, j, i, j);
        context.fill(RenderLayer.getGuiOverlay(), 0, n, scaledWidth, scaledHeight, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, scaledWidth, l, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), 0, l, k, n, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), m, l, scaledWidth, n, -90, -16777216);
    }

    @Unique
    private static void renderShieldOverlay(MinecraftClient client, MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, SHIELD_OVERLAY_TEXTURE);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        BlockPos blockPos = BlockPos.ofFloored(client.player.getX(), client.player.getEyeY(), client.player.getZ());
        float f = LightmapTextureManager.getBrightness(client.player.getWorld().getDimension(), client.player.getWorld().getLightLevel(blockPos));
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(f, f, f, 0.1f);
        float g = 4.0f;
        float h = -1.0f;
        float i = 1.0f;
        float j = -1.0f;
        float k = 1.0f;
        float l = -0.5f;
        float m = -client.player.getYaw() / 64.0f;
        float n = client.player.getPitch() / 64.0f;
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, -1.0f, -1.0f, -0.5f).texture(4.0f + m, 4.0f + n).next();
        bufferBuilder.vertex(matrix4f, 1.0f, -1.0f, -0.5f).texture(0.0f + m, 4.0f + n).next();
        bufferBuilder.vertex(matrix4f, 1.0f, 1.0f, -0.5f).texture(0.0f + m, 0.0f + n).next();
        bufferBuilder.vertex(matrix4f, -1.0f, 1.0f, -0.5f).texture(4.0f + m, 0.0f + n).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}
