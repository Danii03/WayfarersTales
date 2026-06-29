package net.danskii.wayfarers_tales;

import net.danskii.wayfarers_tales.player.PlayerLifeContainerEvents;
import net.danskii.wayfarers_tales.registry.ModItemGroupRegistry;
import net.danskii.wayfarers_tales.registry.ModItemRegistry;
import net.danskii.wayfarers_tales.registry.ModSoundRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Wayfarers_tales implements ModInitializer {
    public static final String MOD_ID = "wayfarers_tales";

    @Override
    public void onInitialize() {
        ModSoundRegistry.initialize();
        ModItemRegistry.initialize();
        ModItemGroupRegistry.initialize();
        PlayerLifeContainerEvents.initialize();
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
