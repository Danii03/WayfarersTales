package net.danskii.wayfarers_tales.registry;

import java.util.function.Function;

import net.danskii.wayfarers_tales.Wayfarers_tales;
import net.danskii.wayfarers_tales.block.ShorterGrassBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModBlockRegistry {
    public static final Block SHORTER_GRASS = registerBlockWithItem("shorter_grass", settings -> new ShorterGrassBlock(
            settings.offset(AbstractBlock.OffsetType.XZ)
    ));

    private ModBlockRegistry() {
    }

    public static void initialize() {
    }

    private static Block registerBlockWithItem(String path, Function<AbstractBlock.Settings, Block> factory) {
        Identifier id = Wayfarers_tales.id(path);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Block block = factory.apply(AbstractBlock.Settings.copy(Blocks.SHORT_GRASS).registryKey(blockKey));
        Block registeredBlock = Registry.register(Registries.BLOCK, blockKey, block);

        Registry.register(Registries.ITEM, itemKey, new BlockItem(
                registeredBlock,
                new Item.Settings().registryKey(itemKey)
        ));

        return registeredBlock;
    }
}
