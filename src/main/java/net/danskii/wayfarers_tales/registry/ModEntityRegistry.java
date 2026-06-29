package net.danskii.wayfarers_tales.registry;

import net.danskii.wayfarers_tales.Wayfarers_tales;
import net.danskii.wayfarers_tales.entity.wisp.WispEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public final class ModEntityRegistry {
    private static final int WISP_SPAWN_WEIGHT = 3;
    private static final int WISP_MIN_GROUP_SIZE = 1;
    private static final int WISP_MAX_GROUP_SIZE = 2;

    public static final EntityType<WispEntity> WISP = registerEntity("wisp",
            FabricEntityTypeBuilder.createMob()
                    .entityFactory(WispEntity::new)
                    .spawnGroup(SpawnGroup.AMBIENT)
                    .dimensions(EntityDimensions.fixed(0.7F, 0.8F))
                    .trackRangeBlocks(64)
                    .trackedUpdateRate(3)
                    .defaultAttributes(WispEntity::createWispAttributes)
                    .spawnRestriction(SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WispEntity::canSpawn)
    );

    public static final Item WISP_SPAWN_EGG = registerSpawnEgg("wisp_spawn_egg", WISP);

    private ModEntityRegistry() {
    }

    public static void initialize() {
        BiomeModifications.addSpawn(
                BiomeSelectors.tag(BiomeTags.IS_FOREST),
                SpawnGroup.AMBIENT,
                WISP,
                WISP_SPAWN_WEIGHT,
                WISP_MIN_GROUP_SIZE,
                WISP_MAX_GROUP_SIZE
        );
    }

    private static <T extends WispEntity> EntityType<T> registerEntity(String path, FabricEntityTypeBuilder.Mob<T> builder) {
        Identifier id = Wayfarers_tales.id(path);
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);
        return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
    }

    private static Item registerSpawnEgg(String path, EntityType<?> entityType) {
        Identifier id = Wayfarers_tales.id(path);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        SpawnEggItem item = new SpawnEggItem(new Item.Settings().registryKey(key).spawnEgg(entityType));
        return Registry.register(Registries.ITEM, key, item);
    }
}
