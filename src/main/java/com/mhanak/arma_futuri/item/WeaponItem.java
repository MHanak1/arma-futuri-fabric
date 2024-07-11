package com.mhanak.arma_futuri.item;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.networking.ModPackets;
import com.mhanak.arma_futuri.registry.ModDamageTypes;
import com.mhanak.arma_futuri.sound.NotifyReloadedSoundInstance;
import com.mhanak.arma_futuri.sound.RifleSoundInstance;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import com.mhanak.arma_futuri.util.MinecraftClientAccess;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.quasar.data.ParticleSettings;
import foundry.veil.api.quasar.particle.ParticleEmitter;
import foundry.veil.api.quasar.particle.ParticleSystemManager;
import foundry.veil.ext.EntityExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class WeaponItem extends Item implements Vanishable {

    public abstract float getProjectileSpeed();
    public abstract float getRange();

    public WeaponItem(Settings settings) {
        super(settings.maxCount(1));
    }

    public abstract boolean isSidearm();

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
        //return super.allowNbtUpdateAnimation(player, hand, oldStack, newStack);
    }

    public abstract float getDamageFalOff();

    public abstract float getDamage();

    public float getDamage(float distance){
        float damageFactor = distance/getRange();
        if (damageFactor > 1.0f) return 0;
        return MathHelper.lerp(damageFactor, getDamage(), getDamage()*(1-getDamageFalOff()));
    }

    public void setCanFireIn(ItemStack stack, int canFireIn){
        stack.getOrCreateNbt().putInt("CanFireIn", canFireIn);
    }

    public int getCanFireIn(ItemStack stack){
        return stack.getOrCreateNbt().getInt("CanFireIn");
    }

    public abstract float getSoundRange();

    public abstract SoundEvent getFireSound();

    public SoundEvent readyToFireSound() {
        return null;
    }

    public int readyToFireSoundDelay(){
        return -3;
    }

    //set it to more than 0 pls
    protected abstract int shootDelay();

    public abstract float neededEnergy();

    public boolean canShoot(ItemStack stack, @Nullable Entity holder){
        if (holder != null) {
            if (holder instanceof PlayerEntity) {
                if (((PlayerEntity) holder).getInventory().getSlotWithStack(stack) != 0 && !isSidearm()) {
                    if (holder.getWorld().isClient()) {
                        MinecraftClient.getInstance().player.sendMessage(Text.of("This weapon must be placed in the 1st slot to be used"));
                    }
                    return false;
                }
            }
            if (ArmorData.getWeaponEnergy((IEntityAccess) holder) < neededEnergy()) return false;
        }
        return this.getCanFireIn(stack) <= 0;
    }

    public abstract boolean isAutomatic();

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            if (MinecraftClient.getInstance().player == user) {
                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeBoolean(true);
                ClientPlayNetworking.send(ModPackets.AIM_ID, buffer);
            }
        }
        ((IEntityAccess)user).setAiming(true);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (this.getCanFireIn(stack) > 0) {
            this.setCanFireIn(stack, this.getCanFireIn(stack)-1);
            if (world.isClient()) {
                if (this.getCanFireIn(stack) == -readyToFireSoundDelay() && getFireSound() != null) {
                    playReadyToFireSound(entity);
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }



    @Environment(EnvType.CLIENT)
    private void playReadyToFireSound(Entity entity){
        if(entity == MinecraftClient.getInstance().player){
            MinecraftClient.getInstance().getSoundManager().play(new NotifyReloadedSoundInstance(MinecraftClient.getInstance().player, readyToFireSound()));
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isClient()) {
            if (MinecraftClient.getInstance().player == user) {
                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeBoolean(false);
                ClientPlayNetworking.send(ModPackets.AIM_ID, buffer);
            }
        }
        ((IEntityAccess)user).setAiming(false);

        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    public abstract float getFovMultiplier();

    public abstract float getRecoilStrength();

    public abstract boolean hasScope();

    public Identifier getScopeTexture(){
        return ArmaFuturiMod.path("textures/misc/default_scope_overlay.png");
    }

    public abstract Vec3d getInHandOffset();

    //for now the server assumes the player is fair and believes every shot. in the future the return will dictate wether the server accepted the shot
    public boolean revieveShotAsServer(PlayerEntity shooter, Entity target, float distance, boolean headshot){
        target.timeUntilRegen = 0;
        ((IEntityAccess) target).arma_futuri$scheldueDamage(ModDamageTypes.rifle_shot(shooter.getWorld(), shooter), (int) (distance / getProjectileSpeed()), headshot ? getDamage(distance) * 1.5f : getDamage(distance));
        return true;
    }

    @Environment(EnvType.CLIENT)
    public void shootAsShooter(World world, PlayerEntity user, ItemStack stack){
        if (!this.canShoot(stack, user)) return;
        setCanFireIn(stack, shootDelay());

        //------------ calculate shooting ------------
        Vec3d otherPos = user.getEyePos().add(user.getRotationVector().multiply(getRange()));
        BlockHitResult hitResult = world.raycast(new RaycastContext(user.getEyePos(), otherPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));

        boolean hitSomething = false;

        Entity hitEntity = null;

        Box box = new Box(user.getEyePos(), otherPos);
        List<Entity> potentialColliderEntities = world.getOtherEntities(user, box);

        float closestDistanceSquared = Float.MAX_VALUE;

        Optional<Vec3d> raycast;
        Optional<Vec3d> entityRaycast = Optional.empty();


        for (Entity entity : potentialColliderEntities) {
            raycast = entity.getBoundingBox().raycast(user.getEyePos(), otherPos);
            if (raycast.isPresent() && !entity.equals(user)) {
                float distance = (float) entity.getEyePos().squaredDistanceTo(user.getEyePos());
                if (distance < closestDistanceSquared) {
                    closestDistanceSquared = distance;
                    hitEntity = entity;
                    entityRaycast = raycast;
                }
            }
        }

        if (hitResult.getPos().squaredDistanceTo(user.getEyePos()) > closestDistanceSquared) {
            if (hitEntity != null) {
                if (hitEntity instanceof EndCrystalEntity) {
                    hitEntity.kill();
                } else if (hitEntity.canBeHitByProjectile()) {
                    float distance = (float) Math.sqrt(closestDistanceSquared);
                    boolean hitHead = false;
                    double headY = hitEntity.getY() + hitEntity.getHeight() - 2 * (hitEntity.getHeight() - hitEntity.getStandingEyeHeight());
                    if (entityRaycast.get().y > headY) hitHead = true;
                    ArmaFuturiMod.LOGGER.info("Y: {}, eye Y: {}, neck Y: y>{}, hit pos: {}, hit head: {}", hitEntity.getY(), hitEntity.getEyeY(), headY, entityRaycast.get().y, hitHead);

                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeInt(hitEntity.getId());
                    buffer.writeFloat(distance);
                    buffer.writeBoolean(hitHead);
                    ClientPlayNetworking.send(ModPackets.SHOOT_ID, buffer);
                    hitSomething = true;
                }
            }
        }

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(0);
        buffer.writeFloat(0);
        buffer.writeBoolean(false);
        if (!hitSomething) {
            ClientPlayNetworking.send(ModPackets.SHOOT_ID, buffer);
        }

        showShootEffects(user);

        ArmorData.setWeaponEnergy((IEntityAccess) user, ArmorData.getWeaponEnergy((IEntityAccess) user) - neededEnergy());
        //------------ end calculate shooting ------------
    }


    @Environment(EnvType.CLIENT)
    public void showShootEffects(PlayerEntity user){
        assert MinecraftClient.getInstance().player != null;
        World world =  user.getWorld();
        if (user.getUuid() == MinecraftClient.getInstance().player.getUuid()) {
            Random random = new Random();

            float direction = random.nextFloat()-0.5f;
            float scale = (random.nextFloat()/5 + 0.9f) * getRecoilStrength();
            if (!((IEntityAccess)user).isAiming()) scale *=2;
            ((MinecraftClientAccess) MinecraftClient.getInstance()).arma_futuri$addCameraVelocity((float) Math.sin(direction) * scale, -(float) Math.cos(direction) * scale);

        }

        //world.playSound(null, BlockPos.ofFloored(user.getPos()), getFireSound(), user.getSoundCategory());
        //user.playSound(getFireSound(), 1.0f, 1.0f);
        MinecraftClient.getInstance().getSoundManager().play(new RifleSoundInstance(user, getFireSound(), getSoundRange()));

        ParticleSystemManager particleManager = VeilRenderSystem.renderer().getParticleManager();
        ParticleEmitter instance = particleManager.createEmitter(new Identifier("arma_futuri:rifle_bullet"));
        if (instance == null) {
            ArmaFuturiMod.LOGGER.error("Particle emitter could not be created!");
        } else {
            Vec3d pos = user.getEyePos().add(user.getRotationVector().multiply(0.5));
            instance.setPosition(new Vector3d(pos.x, pos.y, pos.z));

            instance.setParticleSettings(new ParticleSettings(getProjectileSpeed(), 0.05f, 0, (int) Math.ceil(getRange()/getProjectileSpeed()), 0, user.getRotationVector().toVector3f(), false, true, false, false, false));

            ((EntityExtension) user).veil$addEmitter(instance);

            particleManager.addParticleSystem(instance);

        }
    }
}
