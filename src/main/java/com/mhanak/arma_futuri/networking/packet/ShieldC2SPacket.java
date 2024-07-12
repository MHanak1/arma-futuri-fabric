package com.mhanak.arma_futuri.networking.packet;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.entity.custom.PersonalShieldEntity;
import com.mhanak.arma_futuri.item.ArmorItemWithExpansions;
import com.mhanak.arma_futuri.item.ExpansionItem;
import com.mhanak.arma_futuri.registry.ExpansionItems;
import com.mhanak.arma_futuri.networking.ModPackets;
import com.mhanak.arma_futuri.registry.ModEntities;
import com.mhanak.arma_futuri.util.ArmorData;
import com.mhanak.arma_futuri.util.IEntityAccess;
import com.mhanak.arma_futuri.util.ILivingEntityAccess;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.event.GameEvent;

public class ShieldC2SPacket {
    public static void recieve(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        //boolean lightEnabled = ((IEntityDataSaver)player).getPersistentData().getBoolean("FlashightEnabled");
        boolean enabled = buf.readBoolean();
        ArmaFuturiMod.LOGGER.debug("C2S set the shield to {}", enabled);
        if (enabled) {
            if (ArmorItemWithExpansions.hasExpansion(player, (ExpansionItem) ExpansionItems.SHIELD_MODULE)) {
                Entity shield = ModEntities.PERSONAL_SHIELD.spawn(player.getServerWorld(), player.getBlockPos(), SpawnReason.TRIGGERED);
                ((PersonalShieldEntity) shield).setHealth(ArmorData.getShieldHealth(player));
                ((ILivingEntityAccess) shield).attachToEntity(player);
            }
        }else{
            Entity shield = ((ILivingEntityAccess)player).getAttachedEntity();
            if (shield != null) {
                if (shield instanceof PersonalShieldEntity) {
                    ArmorData.setShieldHealth(player, ((PersonalShieldEntity) shield).getHealth());
                    shield.remove(Entity.RemovalReason.DISCARDED);
                    shield.emitGameEvent(GameEvent.ENTITY_DIE);
                }
            }
        }
        NbtCompound nbt = ((IEntityAccess) player).arma_futuri$getPersistentData();
        nbt.putBoolean("ShieldActive", enabled);

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(enabled);
        buffer.writeUuid(player.getUuid());

        for (PlayerEntity other_player : player.getWorld().getPlayers()){
            ServerPlayNetworking.send((ServerPlayerEntity) other_player, ModPackets.SHIELD_SYNC_ID, buffer);
        }


    }
}
