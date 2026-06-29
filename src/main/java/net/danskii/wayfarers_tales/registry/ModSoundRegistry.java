package net.danskii.wayfarers_tales.registry;

import net.danskii.wayfarers_tales.Wayfarers_tales;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSoundRegistry {
    public static final SoundEvent LIFE_VESSEL = register("life_vessel");
    public static final SoundEvent NOPE_SOUND = register("nope_sound");
    public static final SoundEvent WISP = register("wisp");
    public static final SoundEvent WISP_HURT = register("wisp_hurt");

    private ModSoundRegistry() {
    }

    public static void initialize() {
    }

    private static SoundEvent register(String path) {
        Identifier id = Wayfarers_tales.id(path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}
