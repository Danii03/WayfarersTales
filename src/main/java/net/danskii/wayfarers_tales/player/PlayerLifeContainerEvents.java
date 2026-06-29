package net.danskii.wayfarers_tales.player;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public final class PlayerLifeContainerEvents {
    private PlayerLifeContainerEvents() {
    }

    public static void initialize() {
        ServerPlayerEvents.JOIN.register(PlayerLifeContainerService::applyStoredHeartContainers);
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                PlayerLifeContainerService.applyStoredHeartContainers(newPlayer)
        );
    }
}
