package com.mhanak.arma_futuri.entity.projectile;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

public class RifleProjectileEntity extends PersistentProjectileEntity {

    public boolean landed = false;
    public int timeLanded = 0;
    public PointLight pointLight;

    public PointLight getPointLight() {
        return this.pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public void removePointLight() {
        if (this.getPointLight() != null && this.getWorld().isClient){
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.getPointLight());
        }
    }

    public RifleProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void remove(RemovalReason reason) {
        this.removePointLight();
        super.remove(reason);
    }

    @Override
    public double getDamage() {
        return 10;
        //return super.getDamage();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            this.removePointLight();
        }
        landed = true;
        super.onCollision(hitResult);
    }


    @Override
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 10.0;
        if (Double.isNaN(d)) {
            d = 1.0;
        }
        //quadruple render distance for bullets
        return distance < (d *= 256.0 * PersistentProjectileEntity.getRenderDistanceMultiplier()) * d;
    }

    @Override
    public void tick() {
        if (landed){
            timeLanded++;
        } if (timeLanded > 4) {
            try{
                this.removePointLight();
                this.kill();
            } catch (Exception e){
                ArmaFuturiMod.LOGGER.error("Minecraft for some reason shided itself");
                e.printStackTrace();
            }
        }

        //custom collision detection cause minecraft struggles with fast moving projectiles
        Box box = new Box(this.getPos(), this.getPos().add(this.getVelocity()));
        List<Entity> potentialColliderEntities = this.getWorld().getOtherEntities(this, box);

        EntityHitResult entityHitResult = null;
        double closestDistanceSquared = Double.MAX_VALUE;

        for (Entity entity : potentialColliderEntities) {
            if (entity.getBoundingBox().raycast(this.getPos(), this.getPos().add(this.getVelocity())).isPresent() && !entity.equals(this.getOwner())) {
                double distance = entity.getPos().squaredDistanceTo(this.getPos());
                if (distance < closestDistanceSquared) {
                    closestDistanceSquared = distance;
                    entityHitResult = new EntityHitResult(entity);
                }
                landed = true;
            }
        }

        HitResult hitResult = this.getWorld().raycast(new RaycastContext(this.getPos(), this.getPos().add(this.getVelocity()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));

        if (entityHitResult != null && this.getPos().squaredDistanceTo(hitResult.getPos()) > closestDistanceSquared){
            ArmaFuturiMod.LOGGER.info("hitting entity " + entityHitResult.getEntity());
            this.onCollision(entityHitResult);
        }

        super.tick();
    }
/*
    @Override

    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(this.getWorld(), this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(5.0), this::canHit, 0.3f);
    }
 */
}
