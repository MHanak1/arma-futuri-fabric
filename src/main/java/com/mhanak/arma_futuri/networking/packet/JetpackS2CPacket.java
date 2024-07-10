package com.mhanak.arma_futuri.networking.packet;

import com.mhanak.arma_futuri.util.IEntityAccess;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class JetpackS2CPacket {
    public static void recieve(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean enabled = buf.readBoolean();
        PlayerEntity player = client.world.getPlayerByUuid(buf.readUuid());

        if (player != null) {
            NbtCompound nbt = ((IEntityAccess) player).arma_futuri$getPersistentData();
            nbt.putBoolean("jetpackActive", enabled);
        }

    }
}
