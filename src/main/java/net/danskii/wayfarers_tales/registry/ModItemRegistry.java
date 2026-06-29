package net.danskii.wayfarers_tales.registry;

import java.util.function.Function;

import net.danskii.wayfarers_tales.Wayfarers_tales;
import net.danskii.wayfarers_tales.item.VesselOfLifeItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ModItemRegistry {
    public static final Item VESSEL_OF_LIFE = register("vessel_of_life", settings ->
            new VesselOfLifeItem(settings.maxCount(16).rarity(Rarity.RARE))
    );

    private ModItemRegistry() {
    }

    public static void initialize() {
    }

    private static Item register(String path, Function<Item.Settings, Item> factory) {
        Identifier id = Wayfarers_tales.id(path);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = factory.apply(new Item.Settings().registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }
}
