package com.mhanak.arma_futuri.networking.packet;

import com.mhanak.arma_futuri.item.WeaponItem;
import com.mhanak.arma_futuri.networking.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ShootC2SPacket {
    public static void recieve(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        Entity hitEntity = player.getWorld().getEntityById(buf.readInt());
        float distance = buf.readFloat();
        boolean hitHead = buf.readBoolean();

        if (distance == 0){
            for (PlayerEntity other_player : player.getWorld().getPlayers()){
                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeUuid(player.getUuid());

                if (player != other_player) {
                    ServerPlayNetworking.send((ServerPlayerEntity) other_player, ModPackets.SHOOT_SYNC_ID, buffer);
                }
            }
        }else if (player.getStackInHand(player.getActiveHand()).getItem() instanceof WeaponItem && hitEntity != null){
            ((WeaponItem) player.getStackInHand(player.getActiveHand()).getItem()).revieveShotAsServer( player, hitEntity, distance, hitHead);
        }

    }
}
