package com.mhanak.arma_futuri.entity.custom;


import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import com.mhanak.arma_futuri.util.ILivingEntityAccess;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityAttachmentType; //1.21
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class PersonalShieldEntity extends PassiveEntity  {

    public PointLight pointLight;



    public PersonalShieldEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createPersonalShieldAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30)
                ;
    }


    public boolean collidesWith(Entity other) {
        return canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other) {
        //return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
        return false;
    }

    @Override
    public void setOnFire(boolean onFire) {
        this.setFireTicks(0);
    }


    public boolean isCollidable() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    protected void pushAway(Entity entity) {
        // do nothing
    }

    /*
    public Vec3d getPos() {
        if (this.getVehicle() != null) {
            return this.getVehicle().getPos();
        }
        else return super.getPos();
    }

    public Vec3d getSyncedPos(){
        if (this.getVehicle() != null) {
            return this.getVehicle().getSyncedPos();
        }
        else return super.getSyncedPos();
    }
*/

    @Override
    public boolean damage(DamageSource source, float amount) {
        this.lastDamageTaken = amount;
        this.applyDamage(source, amount);
        this.maxHurtTime = 10;
        this.hurtTime = this.maxHurtTime;
        ArmorData.setShieldHealth(
                (PlayerEntity) ((ILivingEntityAccess)this).getAttachmentEntity(),
                ArmorData.getShieldHealth(
                    ((ILivingEntityAccess)this).getAttachmentEntity()
                ) - amount
        );
        return true;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (((ILivingEntityAccess)this).getAttachmentEntity() != null) {
            Entity player = ((ILivingEntityAccess)this).getAttachmentEntity();
            NbtCompound nbt =((IEntityAccess)  player).arma_futuri$getPersistentData();
            nbt.putFloat("ShieldHealth", this.getHealth());
            ArmorData.setShieldHealth(
                    (PlayerEntity) player,
                    ArmorData.getShieldHealth(
                            ((ILivingEntityAccess)this).getAttachmentEntity()
                    )
            );
            ((ILivingEntityAccess)this).detachFromEntity();
            ArmorData.setShieldActive((IEntityAccess) player, false);
        }
        super.remove(reason);
    }

    public void handleStatus(byte status) {

    }

    public final void updateAttachedEntityPosition(Entity passenger) {
        this.updatePassengerPosition(passenger, Entity::setPosition);
    }

    protected void updateAttachedEntityPosition(Entity passenger, PositionUpdater positionUpdater) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        positionUpdater.accept(passenger, this.getX(), this.getY(), this.getZ());
    }

    @Override
    public void tick() {
        this.setVelocity(Vec3d.ZERO);

        //if (this.getVehicle() != null) {
        //    ((IEntityDataSaver)this).attachToEntity(this.getVehicle());
        //}

        this.timeUntilRegen = 0;
        //this.hurtTime = this.maxHurtTime;
        this.setNoGravity(true);
        this.setNoDrag(true);

        try{

            if (((ILivingEntityAccess) this).getAttachmentEntity() == null) {
                if (!this.getWorld().isClient){
                    ArmaFuturiMod.LOGGER.error("No attachment entity found");
                    this.remove(Entity.RemovalReason.KILLED);
                    this.emitGameEvent(GameEvent.ENTITY_DIE);
                }
            }
            else if (!((ILivingEntityAccess) this).getAttachmentEntity().isAlive()) {
                ArmaFuturiMod.LOGGER.error("Attachment entity is not alive");
                this.remove(Entity.RemovalReason.KILLED);
                this.emitGameEvent(GameEvent.ENTITY_DIE);
            }else{
                //don't ask me why is this nessesary, but it is
                if (this.getHealth() > 0.1) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(0.1f);
                    }
                    Entity attachmentEntity = ((ILivingEntityAccess) this).getAttachmentEntity();
                    NbtCompound nbt  = ((IEntityAccess)attachmentEntity).arma_futuri$getPersistentData();
                    nbt.putFloat("ShieldHealth", this.getHealth());
                    if (!this.getWorld().isClient) {
                        this.setPos(attachmentEntity.getX(), attachmentEntity.getY(), attachmentEntity.getZ());
                    }

                }
                else {
                    this.kill();
                }
            }

            super.tick();


        } catch (Exception e){
            ArmaFuturiMod.LOGGER.error("Shield entity encountered an exception ({}), killing the entity", e);
            this.remove(Entity.RemovalReason.KILLED);
            this.emitGameEvent(GameEvent.ENTITY_DIE);
        }


    }


/*
    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y-1.5f, z);
    }

 //*/

    /*
        //1.21

        public Vec3d getVehicleAttachmentPos(Entity vehicle) {
            return this.getAttachments().getPoint(EntityAttachmentType.VEHICLE, 0, 0).add(0, 2, 0);
        }
    */
    public boolean canHit() {
        return false;
    }
    public boolean canBeHitByProjectile() { return this.getHealth() > 0.1;}

    public boolean isInsideWall() {
        return false;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

}

