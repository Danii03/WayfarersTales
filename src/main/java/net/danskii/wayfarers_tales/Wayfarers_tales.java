package net.danskii.wayfarers_tales;

import net.danskii.wayfarers_tales.player.PlayerLifeContainerEvents;
import net.danskii.wayfarers_tales.event.ForestFireflyAmbience;
import net.danskii.wayfarers_tales.registry.ModBlockRegistry;
import net.danskii.wayfarers_tales.registry.ModItemGroupRegistry;
import net.danskii.wayfarers_tales.registry.ModItemRegistry;
import net.danskii.wayfarers_tales.registry.ModSoundRegistry;
import net.danskii.wayfarers_tales.world.feature.ModVegetationFeatures;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Wayfarers_tales implements ModInitializer {
    public static final String MOD_ID = "wayfarers_tales";

    @Override
    public void onInitialize() {
        ModSoundRegistry.initialize();
        ModBlockRegistry.initialize();
        ModItemRegistry.initialize();
        ModItemGroupRegistry.initialize();
        ModVegetationFeatures.initialize();
        ForestFireflyAmbience.initialize();
        PlayerLifeContainerEvents.initialize();
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
