package net.danskii.wayfarers_tales.world.feature;

import net.danskii.wayfarers_tales.Wayfarers_tales;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public final class ModVegetationFeatures {
    public static final RegistryKey<PlacedFeature> PATCH_SHORTER_GRASS = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE,
            Wayfarers_tales.id("patch_shorter_grass")
    );

    private ModVegetationFeatures() {
    }

    public static void initialize() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.VEGETAL_DECORATION,
                PATCH_SHORTER_GRASS
        );
    }
}
