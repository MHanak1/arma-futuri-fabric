package com.mhanak.arma_futuri.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import com.mhanak.arma_futuri.item.ExpansionItem;
import com.mhanak.arma_futuri.registry.ExpansionItems;
import com.mhanak.arma_futuri.sound.JetpackSoundInstance;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import foundry.veil.ext.EntityExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow @Final private PlayerInventory inventory;
    private ItemStack lastMainWeapon = ItemStack.EMPTY;

    @Unique
    @Environment(EnvType.CLIENT)
    private SoundInstance jetpackSound;

    @ModifyArg(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    float modifyDamage(DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.BYPASSES_ARMOR) || source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) return amount;
        float newAmount = amount - ArmorData.getTotalDamageAbsorbtion((PlayerEntity) (Object)this);
        if (source.isOf(DamageTypes.LAVA)){
            newAmount = newAmount/2 + amount/2;
        }
        return MathHelper.clamp(newAmount, 0, 100000);
    }

    @ModifyReturnValue(method = "getBlockBreakingSpeed", at = @At(value = "RETURN"))
    float modifyBlockBreakingSpeed(float speed) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.isSubmergedIn(FluidTags.WATER)) {
            for (ItemStack stack : player.getArmorItems()){
                if (stack.getItem() instanceof ArmorItemWithExpansions && ((ArmorItem) stack.getItem()).getType() == ArmorItem.Type.CHESTPLATE ){
                    if (((ArmorItemWithExpansions) stack.getItem()).isSealed()){
                        return speed*5;
                    }
                }
            }
        }
        return speed;
    }

    @Unique
    private float getMaxJetpackVelocity(){
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.isSubmergedInWater()) return (0.3f / ArmorData.getArmorWheight(player));
        else return (1f / ArmorData.getArmorWheight(player));
    }

    @Inject(method = "getMovementSpeed", at = @At("HEAD"), cancellable = true)
    private void getMovementSpeed(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue ( (float) ((PlayerEntity)(Object)this).getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * ArmorData.getTotalSpeedModifier((PlayerEntity)(Object)this));

    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        float fuel = ArmorData.getJetpackFuel((IEntityAccess) this);
        float maxFuel = ArmorData.getMaxFuel(player);

        if (inventory.main.get(0) != lastMainWeapon){
            ArmorData.setWeaponEnergy((IEntityAccess) this, (ArmorData.getMaxWeaponEnergy((IEntityAccess) player) / -2f));
        }
        lastMainWeapon = inventory.main.get(0);


        if (ArmorItemWithExpansions.hasExpansion((Entity) (Object) this, ExpansionItems.JETPACK) && fuel > 0 && !player.isSwimming() && !player.isOnGround() && !player.getAbilities().flying) {
            if (ArmorData.getJetpackActive((IEntityAccess) this)) {
                if (player.getVelocity().y < getMaxJetpackVelocity()) {
                    if (player.getVelocity().y < 0) {
                        player.addVelocity(0, 0.2 / ArmorData.getArmorWheight(player), 0);
                    } else {
                        player.addVelocity(0, 0.12 / ArmorData.getArmorWheight(player), 0);
                    }
                }
                player.limitFallDistance();
                ArmorData.setJetpackFuel((IEntityAccess) this, fuel - (0.5f * ArmorData.getArmorWheight(player)));

                if (player.getWorld().isClient) makeJetpackSound();
            } else if (player.isSneaking()) {
                double VFall = player.getVelocity().y + 0.12;
                if (VFall < -1) {
                    VFall = -1;
                } else if (VFall > 0) {
                    VFall = 0;
                }
                player.addVelocity(0, (VFall / -5) / ArmorData.getArmorWheight(player), 0);
                player.limitFallDistance();

                ArmorData.setJetpackFuel((IEntityAccess) this, fuel - (0.2f * ArmorData.getArmorWheight(player)));
                if (player.getWorld().isClient) makeJetpackSound();
            }else if (player.getWorld().isClient) {
                stopJetpackSound();
            }
            //((LivingEntity)(Object) this).fallDistance = 1f;
        }
        else if (player.getWorld().isClient) {
            stopJetpackSound();
        }

        if (player.isOnGround() || player.getAbilities().flying) {
            if (fuel != maxFuel){
                if (fuel + 2f > maxFuel){
                    fuel = maxFuel;
                }
                else fuel += 2f;
                ArmorData.setJetpackFuel((IEntityAccess) this, fuel);
            }
        }


        float weaponEnergy = ArmorData.getWeaponEnergy((IEntityAccess) this);
        float maxWeaponEnergy = ArmorData.getMaxWeaponEnergy((IEntityAccess) this);

        float neededEnergy = MathHelper.clamp(maxWeaponEnergy-weaponEnergy, 0, 1f);

        if (neededEnergy != 0){
            ArmorData.setWeaponEnergy((IEntityAccess) this, neededEnergy + weaponEnergy);
        }
    }


    @Unique
    @Environment(EnvType.CLIENT)
    private void makeJetpackSound(){
        if (jetpackSound == null) {
            jetpackSound = new JetpackSoundInstance(((PlayerEntity)(Object)this));
            MinecraftClient.getInstance().getSoundManager().play(jetpackSound);
        }
    }


    @Unique
    @Environment(EnvType.CLIENT)
    private void stopJetpackSound(){
        PlayerEntity player = ((PlayerEntity)(Object)this);
        EntityExtension extension = (EntityExtension) player.getWorld().getPlayerByUuid(((PlayerEntity) (Object)this).getUuid());
        if (jetpackSound != null) {
            MinecraftClient.getInstance().getSoundManager().stop(jetpackSound);
            jetpackSound = null;
        }
    }

    @Inject(method = "checkFallFlying", at = @At("HEAD"), cancellable = true)
    private void checkFallFlying(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!player.isOnGround()){
            ArmorData.setJetpackActive((IEntityAccess) player, true);
            if (ArmorItemWithExpansions.hasExpansion((Entity) (Object) this, ExpansionItems.JETPACK)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "getOffGroundSpeed", at = @At("HEAD"), cancellable = true)
    private void getOffGroundSpeed(CallbackInfoReturnable<Float> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (
            ArmorData.getJetpackActive((IEntityAccess) player) &&
            ArmorItemWithExpansions.hasExpansion(player, (ExpansionItem) ExpansionItems.JETPACK) &&
            ArmorData.getJetpackFuel((IEntityAccess) this) > 0
        ){

            cir.setReturnValue(0.1f / ArmorData.getArmorWheight(player));
        }
    }
}

