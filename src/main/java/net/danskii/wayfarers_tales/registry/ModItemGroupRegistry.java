package net.danskii.wayfarers_tales.registry;

import net.danskii.wayfarers_tales.Wayfarers_tales;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItemGroupRegistry {
    public static final RegistryKey<ItemGroup> WAYFARERS_TALES = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Wayfarers_tales.id("wayfarers_tales")
    );

    private ModItemGroupRegistry() {
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.ofVanilla("natural_blocks")))
                .register(entries -> entries.addAfter(Items.SHORT_GRASS, ModBlockRegistry.SHORTER_GRASS));
        ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.ofVanilla("spawn_eggs")))
                .register(entries -> entries.add(ModEntityRegistry.WISP_SPAWN_EGG));

        Registry.register(Registries.ITEM_GROUP, WAYFARERS_TALES, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.wayfarers_tales"))
                .icon(() -> new ItemStack(ModItemRegistry.VESSEL_OF_LIFE))
                .entries((context, entries) -> entries.add(ModItemRegistry.VESSEL_OF_LIFE))
                .build()
        );
    }
}
