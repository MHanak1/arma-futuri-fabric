package com.mhanak.arma_futuri.networking;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.networking.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier FLASHLIGHT_TOGGLE_ID = ArmaFuturiMod.path("flashlight_toggle");
    public static final Identifier FLASHLIGHT_SYNC_ID = ArmaFuturiMod.path("flashlight_sync");
    public static final Identifier SHIELD_TOGGLE_ID = ArmaFuturiMod.path("shield_toggle");
    public static final Identifier SHIELD_SYNC_ID = ArmaFuturiMod.path("shield_sync");
    public static final Identifier AIM_SYNC_ID = ArmaFuturiMod.path("aim_sync");

    public static final Identifier JETPACK_TOGGLE_ID = ArmaFuturiMod.path("jetpack_toggle");
    public static final Identifier JETPACK_SYNC_ID = ArmaFuturiMod.path("jetpack_sync");
    public static final Identifier SHOOT_ID = ArmaFuturiMod.path("shoot");
    public static final Identifier SHOOT_SYNC_ID = ArmaFuturiMod.path("shoot_sync");
    public static final Identifier AIM_ID = ArmaFuturiMod.path("aim");


    public static void registerS2CPackets() {
        ServerPlayNetworking.registerGlobalReceiver(FLASHLIGHT_TOGGLE_ID, FlashlightC2SPacket::recieve);
        ServerPlayNetworking.registerGlobalReceiver(SHIELD_TOGGLE_ID, ShieldC2SPacket::recieve);
        ServerPlayNetworking.registerGlobalReceiver(JETPACK_TOGGLE_ID, JetpackC2SPacket::recieve);
        ServerPlayNetworking.registerGlobalReceiver(SHOOT_ID, ShootC2SPacket::recieve);
        ServerPlayNetworking.registerGlobalReceiver(AIM_ID, AimC2SPacket::recieve);
    }

    public static void registerC2SPackets() {
        ClientPlayNetworking.registerGlobalReceiver(FLASHLIGHT_SYNC_ID, FlashlightS2CPacket::recieve);
        ClientPlayNetworking.registerGlobalReceiver(SHIELD_SYNC_ID, ShieldS2CPacket::recieve);
        ClientPlayNetworking.registerGlobalReceiver(JETPACK_SYNC_ID, JetpackS2CPacket::recieve);
        ClientPlayNetworking.registerGlobalReceiver(SHOOT_SYNC_ID, ShootS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(AIM_SYNC_ID,  AimS2CPacket::recieve);
    }
}

