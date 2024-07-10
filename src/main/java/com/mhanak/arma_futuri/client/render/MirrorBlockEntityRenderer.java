package com.mhanak.arma_futuri.client.render;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.block.MirrorBlock;
import com.mhanak.arma_futuri.blockentity.MirrorBlockEntity;
import com.mhanak.arma_futuri.registry.ModBlocks;
import com.mhanak.arma_futuri.registry.ModRenderTypes;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.CullFrustum;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.FramebufferManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeResource;

import java.lang.Math;
import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

public class MirrorBlockEntityRenderer implements BlockEntityRenderer<MirrorBlockEntity>, NativeResource {

    private static final Identifier MIRROR_FBO = ArmaFuturiMod.path("mirror");
    private static final float RENDER_DISTANCE = 64.0F;

    private static final Matrix4f RENDER_MODELVIEW = new Matrix4f();
    private static final Matrix4f RENDER_PROJECTION = new Matrix4f();
    private static final Vector4f OBLIQUE_PLANE = new Vector4f();
    private static final Quaternionf VIEW = new Quaternionf();

    private static final ObjectSet<BlockPos> RENDER_POSITIONS = new ObjectArraySet<>();
    private static final Long2ObjectMap<MirrorTexture> TEXTURES = new Long2ObjectArrayMap<>();

    public MirrorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(this::free));
    }

    private static long getKey(BlockPos pos, Direction face) {
        Vec3i normal = face.getVector();
        return (long) face.getAxis().ordinal() << 60 | ((long) pos.getX() * normal.getX() + (long) pos.getY() * normal.getY() + (long) pos.getZ() * normal.getZ());
    }

    @Override
    public void render(MirrorBlockEntity blockEntity, float f, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, int j) {
        if (VeilLevelPerspectiveRenderer.isRenderingPerspective()) {
            return;
        }

        BlockPos pos = blockEntity.getPos();
        Direction facing = blockEntity.getCachedState().get(MirrorBlock.FACING);

        RENDER_POSITIONS.add(pos.toImmutable());
        MirrorTexture texture = TEXTURES.get(getKey(pos, facing));
        if (texture == null) {
            return;
        }

        poseStack.push();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(facing.asRotation()));
        poseStack.translate(-0.5, -0.5, -0.5);

        Matrix4f pose = poseStack.peek().getPositionMatrix();
        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder builder = tesselator.getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
        builder.vertex(pose, 0, 0, 0.125F).color(1.0F, 1.0F, 1.0F, 1.0F).texture(1.0F, 0.0F).light(i).next();
        builder.vertex(pose, 1, 0, 0.125F).color(1.0F, 1.0F, 1.0F, 1.0F).texture(0.0F, 0.0F).light(i).next();
        builder.vertex(pose, 1, 1, 0.125F).color(1.0F, 1.0F, 1.0F, 1.0F).texture(0.0F, 1.0F).light(i).next();
        builder.vertex(pose, 0, 1, 0.125F).color(1.0F, 1.0F, 1.0F, 1.0F).texture(1.0F, 1.0F).light(i).next();

        RenderSystem.setShaderColor(0.9F, 0.9F, 0.9F, 1.0F);
        RenderSystem.setShaderTexture(0, texture.getGlId());
        ModRenderTypes.mirror().draw(builder, RenderSystem.getVertexSorting());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.pop();
    }

    @Override
    public int getRenderDistance() {
        return 64;
    }

    //@Override
    public boolean shouldRender(MirrorBlockEntity blockEntity, Vec3d vec3) {
        BlockPos pos = blockEntity.getPos();
        int viewDistance = this.getRenderDistance();
        if (vec3.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) >= viewDistance * viewDistance) {
            return false;
        }

        CullFrustum frustum = VeilRenderer.getCullingFrustum();
        Direction facing = blockEntity.getCachedState().get(MirrorBlock.FACING);
        Vec3i normal = facing.getVector();
        if (dot(pos, normal, vec3.x, vec3.y, vec3.z) >= 0) {
            return false;
        }

        Box box = MirrorBlock.BOUNDING_BOXES[facing.getHorizontal()];
        return frustum.testAab(
                pos.getX() + box.minX,
                pos.getY() + box.minY,
                pos.getZ() + box.minZ,
                pos.getX() + box.maxX,
                pos.getY() + box.maxY,
                pos.getZ() + box.maxZ);
    }

    private static float dot(BlockPos pos, Vec3i normal, double x, double y, double z) {
        return (float) ((pos.getX() + 0.5 - normal.getX() * 0.5 - x) * normal.getX() + (pos.getY() + 0.5 - normal.getY() * 0.5 - y) * normal.getY() + (pos.getZ() + 0.5 - normal.getZ() * 0.5 - z) * normal.getZ());
    }

    public static void renderLevel(ClientWorld level, Matrix4fc projection, float partialTicks, CullFrustum frustum, Camera camera) {
        if (VeilLevelPerspectiveRenderer.isRenderingPerspective() || !projection.isFinite()) {
            return;
        }

        FramebufferManager framebufferManager = VeilRenderSystem.renderer().getFramebufferManager();
        AdvancedFbo fbo = framebufferManager.getFramebuffer(MIRROR_FBO);
        if (fbo == null) {
            return;
        }

        for (BlockPos pos : RENDER_POSITIONS) {
            BlockState state = level.getBlockState(pos);
            if (!state.isOf(ModBlocks.MIRROR)) {
                continue;
            }

            Direction facing = state.get(MirrorBlock.FACING);
            MirrorTexture mirror = TEXTURES.computeIfAbsent(getKey(pos, facing), unused -> new MirrorTexture());
            mirror.positions.add(pos);
            if (mirror.hasRendered()) {
                continue;
            }

            Vec3i normal = facing.getVector();
            Vector3dc cameraPos = frustum.getPosition();

            Vector3d renderPos = new Vector3d(pos.getX() + 0.5 - normal.getX() * 0.375, pos.getY() + 0.5 - normal.getY() * 0.375, pos.getZ() + 0.5 - normal.getZ() * 0.375);
            Vector3f offset = new Vector3f((float) (cameraPos.x() - renderPos.x), (float) (cameraPos.y() - renderPos.y), (float) (cameraPos.z() - renderPos.z));
            Vector4f plane = new Vector4f(normal.getX(), normal.getY(), normal.getZ(), -offset.dot(normal.getX(), normal.getY(), normal.getZ()));

            Window window = MinecraftClient.getInstance().getWindow();
            float aspect = (float) window.getFramebufferWidth() / window.getFramebufferHeight();
            float fov = projection.perspectiveFov();
            RENDER_PROJECTION.setPerspective(fov, aspect, 0.3F, RENDER_DISTANCE * 4);

            offset.reflect(normal.getX(), normal.getY(), normal.getZ());
            renderPos.add(offset);

            Vector3f dir = camera.getHorizontalPlane().reflect(normal.getX(), normal.getY(), normal.getZ(), new Vector3f());
            Vector3f up = camera.getVerticalPlane().reflect(normal.getX(), normal.getY(), normal.getZ(), new Vector3f());

            new Quaternionf().lookAlong(dir, up).transform(plane);

            calculateObliqueMatrix(RENDER_PROJECTION, plane, RENDER_PROJECTION);

            VeilLevelPerspectiveRenderer.render(fbo, RENDER_MODELVIEW, RENDER_PROJECTION, renderPos, VIEW.identity().lookAlong(dir, up), RENDER_DISTANCE, partialTicks);
            mirror.copy(fbo);
            mirror.setRendered(true);
        }

        ObjectIterator<Long2ObjectMap.Entry<MirrorTexture>> iterator = TEXTURES.long2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, MirrorTexture> entry = iterator.next();
            MirrorTexture mirror = entry.getValue();
            mirror.setRendered(false);
            mirror.positions.removeIf(p -> !RENDER_POSITIONS.contains(p));
            if (mirror.positions.isEmpty()) {
                iterator.remove();
                mirror.close();
            }
        }

        RENDER_POSITIONS.clear();
    }

    public static void calculateObliqueMatrix(Matrix4fc projection, Vector4fc c, Matrix4f store) {
        Vector4f q = projection.invert(new Matrix4f()).transform(
                Math.signum(c.x()),
                Math.signum(c.y()),
                1.0f,
                1.0f,
                OBLIQUE_PLANE);
        float dot = c.dot(q);
        store.m02(c.x() * 2.0F / dot - projection.m03()).m12(c.y() * 2.0F / dot - projection.m13()).m22(c.z() * 2.0F / dot - projection.m23()).m32(c.w() * 2.0F / dot - projection.m33());
    }

    @Override
    public void free() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ObjectCollection<MirrorTexture> values = TEXTURES.values();
            IntBuffer textures = stack.mallocInt(values.size());
            values.forEach(texture -> textures.put(texture.getGlId()));
            textures.flip();
            glDeleteTextures(textures);
        }
        TEXTURES.clear();
    }

    private static class MirrorTexture extends AbstractTexture {

        private final ObjectSet<BlockPos> positions;
        private boolean rendered;

        private int width;
        private int height;

        private MirrorTexture() {
            this.positions = new ObjectArraySet<>();
            this.setFilter(false, true);
            this.width = -1;
            this.height = -1;
        }

        @Override
        public void load(ResourceManager resourceManager) {
        }

        public void copy(AdvancedFbo fbo) {
            int id = this.getGlId();
            int width = fbo.getWidth();
            int height = fbo.getHeight();
            if (this.width != width || this.height != height) {
                this.width = width;
                this.height = height;
                TextureUtil.prepareImage(NativeImage.InternalFormat.RGBA, id, 4, width, height);
            }

            RenderSystem.bindTexture(id);
            fbo.bindRead();
            glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);
            AdvancedFbo.unbind();
            glGenerateMipmap(GL_TEXTURE_2D);
        }

        public boolean hasRendered() {
            return this.rendered;
        }

        public void setRendered(boolean rendered) {
            this.rendered = rendered;
        }
    }
}
