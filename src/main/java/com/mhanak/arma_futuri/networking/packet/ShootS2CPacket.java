package com.mhanak.arma_futuri.networking.packet;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.item.WeaponItem;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class ShootS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        try {
            if (client.world != null) {
                PlayerEntity player = client.world.getPlayerByUuid(buf.readUuid());

                if (player != null) {
                    if (!player.getMainHandStack().isEmpty()) {
                        if (player.getMainHandStack().getItem() instanceof WeaponItem) {
                            ((WeaponItem) player.getMainHandStack().getItem()).showShootEffects(player);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            ArmaFuturiMod.LOGGER.error("what the fuck just happened here");
        }
    }
}
