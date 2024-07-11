package com.mhanak.arma_futuri.mixin;

import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import com.mhanak.arma_futuri.item.ExpansionItem;
import com.mhanak.arma_futuri.item.expansion.ExpansionItems;
import com.mhanak.arma_futuri.sound.JetpackSoundInstance;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import foundry.veil.ext.EntityExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Unique
    @Environment(EnvType.CLIENT)
    private SoundInstance jetpackSound;

    @Unique
    private float getMaxJetpackVelocity(){
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.isSubmergedInWater()) return (0.3f / ArmorData.getArmorWheight(player));
        else return (1f / ArmorData.getArmorWheight(player));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        float fuel = ArmorData.getJetpackFuel((IEntityAccess) this);
        float maxFuel = ArmorData.getMaxFuel(player);

        if (ArmorItemWithExpansions.hasExpansion((Entity) (Object) this, ExpansionItems.JETPACK) && fuel > 0 && !player.isSwimming() && !(player.isOnGround() && !player.getAbilities().flying)) {
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
            cir.setReturnValue(false);
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

