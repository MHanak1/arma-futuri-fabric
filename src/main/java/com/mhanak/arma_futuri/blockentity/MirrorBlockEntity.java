package com.mhanak.arma_futuri.blockentity;

import com.mhanak.arma_futuri.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class MirrorBlockEntity extends BlockEntity {

    public MirrorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlocks.MIRROR_BE, blockPos, blockState);
    }

//    @Override
//    public Packet<ClientGamePacketListener> getUpdatePacket() {
//        return ClientboundBlockEntityDataPacket.create(this);
//    }
}
