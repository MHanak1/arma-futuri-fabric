package com.mhanak.arma_futuri.registry;

import com.mhanak.arma_futuri.ArmaFuturiMod;
import com.mhanak.arma_futuri.block.MirrorBlock;
import com.mhanak.arma_futuri.blockentity.MirrorBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {

    public static final Block MIRROR = register("mirror", new MirrorBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK)
                    .nonOpaque()
                    .solidBlock(Blocks::never)
                    .suffocates(Blocks::never)),
            new Item.Settings());
    public static final BlockEntityType<MirrorBlockEntity> MIRROR_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE, ArmaFuturiMod.path("mirror"), BlockEntityType.Builder.create(MirrorBlockEntity::new, MIRROR).build(null));

    public static void bootstrap() {
    }

    public static <T extends Block> T register(String name, T block, Item.Settings properties) {
        register(name, block);
        ModItems.register(name, new BlockItem(block, properties));
        return block;
    }

    public static Block register(String name, Block block) {
        return Registry.register(Registries.BLOCK, ArmaFuturiMod.path(name), block);
    }
}
