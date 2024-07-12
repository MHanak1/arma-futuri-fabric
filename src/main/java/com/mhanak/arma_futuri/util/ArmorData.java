package com.mhanak.arma_futuri.util;

import com.mhanak.arma_futuri.entity.custom.PersonalShieldEntity;
import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import com.mhanak.arma_futuri.item.WeaponItem;
import com.mhanak.arma_futuri.networking.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;

public class ArmorData {
    /*
    public static boolean getJetpackEnabled(IEntityAccess player) {
        NbtCompound nbt = player.getPersistentData();
        return nbt.getBoolean("jetpackEnabled");
    }

    public static void setJetpackEnabled(IEntityAccess player, boolean jetpack) {
        NbtCompound nbt = player.getPersistentData();
        nbt.putBoolean("jetpackEnabled", jetpack);

        if (jetpack){
            if (((PlayerEntity) player).isOnGround()){
                setJetpackActive(player, true);
            }
        }else if (getJetpackActive(player)){
            setJetpackActive(player, false);
        }
    }
     */

    public static float getTotalSpeedModifier(PlayerEntity player) {
        float speed = 1;
        for (ItemStack armorPiece : player.getInventory().armor){
            if (armorPiece.getItem() instanceof ArmorItemWithExpansions){
                speed *= ((ArmorItemWithExpansions) armorPiece.getItem()).getSpeedModifier(armorPiece);
            }
        }
        return speed;
    }

    public static float getTotalDamageAbsorbtion(PlayerEntity player) {
        float armor = 0;
        for (ItemStack armorPiece : player.getInventory().armor){
            if (armorPiece.getItem() instanceof ArmorItemWithExpansions){
                armor += ((ArmorItemWithExpansions) armorPiece.getItem()).getDamageAbsorbtion(armorPiece);
            }
        }
        return armor;
    }

    public static float getArmorWheight(PlayerEntity player) {
        float cummulativeWheight = 0;
        for (ItemStack stack : player.getInventory().armor){
            if (stack.getItem() instanceof ArmorItemWithExpansions){
                cummulativeWheight += ((ArmorItemWithExpansions) stack.getItem()).getArmorWeight(stack);
            }else if (!stack.isEmpty()){
                cummulativeWheight += 1;
            }
        }
        return MathHelper.clamp(cummulativeWheight/player.getInventory().armor.size(), 1, 10);
    }

    public static boolean hasWeaponInHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < playerInventory.main.size(); i++) {
            if (PlayerInventory.isValidHotbarIndex(i)){
                if (playerInventory.main.get(i).getItem() instanceof WeaponItem) return true;
            }
        }
        return false;
    }

    public static boolean getJetpackActive(IEntityAccess player) {
        NbtCompound nbt = player.arma_futuri$getPersistentData();
        return nbt.getBoolean("jetpackActive");
    }

    public static void setJetpackActive(IEntityAccess player, boolean active) {
        //((ClientPlayerEntity)player).sendMessage(Text.of("jetpack is " + (jetpack ? "active" : "inactive")));
        NbtCompound nbt = player.arma_futuri$getPersistentData();
        nbt.putBoolean("jetpackActive", active);

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(active);
        buffer.writeUuid(((PlayerEntity) player).getUuid());

        if(((PlayerEntity) player).getWorld().isClient) {
            ClientPlayNetworking.send(ModPackets.JETPACK_TOGGLE_ID, buffer);
        }
    }

    public static float getJetpackFuel(IEntityAccess player) {
        NbtCompound nbt = player.arma_futuri$getPersistentData();
        return nbt.getFloat("jetpackFuel");
    }

    public static void setJetpackFuel(IEntityAccess player, float fuel) {
        NbtCompound nbt = player.arma_futuri$getPersistentData();
        nbt.putFloat("jetpackFuel", fuel);
    }

    public static float getMaxFuel(PlayerEntity player) {
        float fuel = 0;
        for (ItemStack armorPiece : player.getInventory().armor){
            if (armorPiece.getItem() instanceof ArmorItemWithExpansions){
                fuel += ((ArmorItemWithExpansions) armorPiece.getItem()).getMaxFuel(armorPiece);
            }
        }
        return fuel;
    }

    public static float getWeaponEnergy(IEntityAccess player) {
        NbtCompound nbt = player.arma_futuri$getPersistentData();
        return nbt.getFloat("weaponEnergy");
    }

    public static void setWeaponEnergy(IEntityAccess player, float energy) {
        NbtCompound nbt = player.arma_futuri$getPersistentData();
        nbt.putFloat("weaponEnergy", energy);
    }

    public static float getMaxWeaponEnergy(IEntityAccess player) {
        return 50;
    }

    public static boolean getShieldActive(IEntityAccess player){
        NbtCompound nbt = player.arma_futuri$getPersistentData();
        return nbt.getBoolean("ShieldActive" );
    }

    public static void setShieldActive(IEntityAccess player, boolean active){
        NbtCompound nbt = player.arma_futuri$getPersistentData();
        nbt.putBoolean("ShieldActive", active);

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(active);
        buffer.writeUuid(((PlayerEntity) player).getUuid());

        if(((PlayerEntity) player).getWorld().isClient) {
            ClientPlayNetworking.send(ModPackets.SHIELD_TOGGLE_ID, buffer);
        }
    }

    public static void toggleShieldActive(IEntityAccess player){
        setShieldActive(player, !getShieldActive(player));
    }

    public static float getShieldHealth(Entity player){
        if (((ILivingEntityAccess)player).getAttachedEntity() instanceof PersonalShieldEntity){
            return ((PersonalShieldEntity) ((ILivingEntityAccess)player).getAttachedEntity()).getHealth();
        }
        else {
            NbtCompound nbt = ((IEntityAccess) player).arma_futuri$getPersistentData();
            return nbt.getFloat("ShieldHealth");
        }
    }

    public static void setShieldHealth(PlayerEntity player, float health){
        if (((ILivingEntityAccess)player).getAttachedEntity() instanceof PersonalShieldEntity){
            ((PersonalShieldEntity) ((ILivingEntityAccess)player).getAttachedEntity()).setHealth(health);
        }
        else {
            NbtCompound nbt = ((IEntityAccess) player).arma_futuri$getPersistentData();
            nbt.putFloat("ShieldHealth", health);
        }
    }

    public static boolean getFlashlightActive(IEntityAccess player){
        NbtCompound nbt = player.arma_futuri$getPersistentData();
        return nbt.getBoolean("FlashlightActive");
    }

    public static void setFlashlightActive(IEntityAccess player, boolean active){
        NbtCompound nbt = player.arma_futuri$getPersistentData();
        nbt.putBoolean("FlashlightActive", active);

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(active);
        buffer.writeUuid(((PlayerEntity) player).getUuid());
        if(((PlayerEntity) player).getWorld().isClient) {
            ClientPlayNetworking.send(ModPackets.FLASHLIGHT_TOGGLE_ID, buffer);
        }
    }

    public static void toggleFlashlight(IEntityAccess player){
        setFlashlightActive(player, !getFlashlightActive(player));
    }
}
