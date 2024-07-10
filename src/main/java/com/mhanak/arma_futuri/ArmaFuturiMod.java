package com.mhanak.arma_futuri;

import com.mhanak.arma_futuri.networking.ModPackets;
import com.mhanak.arma_futuri.registry.ModBlocks;
import com.mhanak.arma_futuri.registry.ModEntities;
import com.mhanak.arma_futuri.registry.ModItems;
import com.mhanak.arma_futuri.registry.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmaFuturiMod implements ModInitializer {

    public static final String MODID = "arma_futuri";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    private static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, ArmaFuturiMod.path("items"));

    public static Identifier path(String path) {
        return new Identifier(MODID, path);
    }

    @Override
    public void onInitialize() {
        ModItems.bootstrap();
        ModBlocks.bootstrap();
        ModEntities.bootstrap();
        ModPackets.registerS2CPackets();
        ModSounds.registerSounds();
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder().displayName(Text.translatable(MODID + ".items")).icon(() -> new ItemStack(ModBlocks.MIRROR)).entries(ModItems::fillTab).build());


    }
}