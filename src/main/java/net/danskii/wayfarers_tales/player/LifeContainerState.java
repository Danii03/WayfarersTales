package net.danskii.wayfarers_tales.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.danskii.wayfarers_tales.Wayfarers_tales;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

public class LifeContainerState extends PersistentState {
    public static final int DEFAULT_HEART_CONTAINERS = 3;
    public static final int MAX_HEART_CONTAINERS = 20;

    private static final String STORAGE_ID = Wayfarers_tales.MOD_ID + "_life_containers";

    public static final Codec<LifeContainerState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.INT)
                    .optionalFieldOf("players", Map.of())
                    .forGetter(LifeContainerState::getSavedHeartContainers)
    ).apply(instance, LifeContainerState::new));

    public static final PersistentStateType<LifeContainerState> TYPE = new PersistentStateType<>(
            STORAGE_ID,
            LifeContainerState::new,
            CODEC,
            DataFixTypes.LEVEL
    );

    private final Map<String, Integer> heartContainersByPlayer = new HashMap<>();

    public LifeContainerState() {
    }

    private LifeContainerState(Map<String, Integer> savedHeartContainers) {
        savedHeartContainers.forEach((playerUuid, heartContainers) ->
                heartContainersByPlayer.put(playerUuid, clampHeartContainers(heartContainers))
        );
    }

    public static LifeContainerState get(ServerPlayerEntity player) {
        return player.getEntityWorld()
                .getServer()
                .getOverworld()
                .getPersistentStateManager()
                .getOrCreate(TYPE);
    }

    public int getHeartContainers(UUID playerUuid) {
        return heartContainersByPlayer.computeIfAbsent(playerUuid.toString(), uuid -> {
            markDirty();
            return DEFAULT_HEART_CONTAINERS;
        });
    }

    public boolean canIncreaseHeartContainers(UUID playerUuid) {
        return getHeartContainers(playerUuid) < MAX_HEART_CONTAINERS;
    }

    public int increaseHeartContainers(UUID playerUuid) {
        int currentHeartContainers = getHeartContainers(playerUuid);
        int increasedHeartContainers = clampHeartContainers(currentHeartContainers + 1);
        heartContainersByPlayer.put(playerUuid.toString(), increasedHeartContainers);
        markDirty();
        return increasedHeartContainers;
    }

    private Map<String, Integer> getSavedHeartContainers() {
        return heartContainersByPlayer;
    }

    private static int clampHeartContainers(int heartContainers) {
        return Math.clamp(heartContainers, DEFAULT_HEART_CONTAINERS, MAX_HEART_CONTAINERS);
    }
}
