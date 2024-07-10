package com.mhanak.arma_futuri.networking.packet;

import com.mhanak.arma_futuri.networking.ModPackets;
import com.mhanak.arma_futuri.util.IEntityAccess;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class AimC2SPacket {
    public static void recieve(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        //boolean lightEnabled = ((IEntityDataSaver)player).getPersistentData().getBoolean("FlashightEnabled");
        boolean enabled = buf.readBoolean();
        //ArmorData.setFlashlightActive((IEntityDataSaver) player, enabled);
        ((IEntityAccess)player).setAiming(enabled);

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(enabled);
        buffer.writeUuid(player.getUuid());

        for (PlayerEntity other_player : player.getWorld().getPlayers()){
            ServerPlayNetworking.send((ServerPlayerEntity) other_player, ModPackets.AIM_SYNC_ID, buffer);
            //ArmaFuturiMod.LOGGER.info("Sent FLASHLIGHT packet to {}", other_player.getDisplayName());
        }
    }
}
