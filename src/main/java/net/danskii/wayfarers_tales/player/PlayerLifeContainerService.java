package net.danskii.wayfarers_tales.player;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerLifeContainerService {
    private static final double HEALTH_POINTS_PER_HEART = 2.0D;

    private PlayerLifeContainerService() {
    }

    public static void applyStoredHeartContainers(ServerPlayerEntity player) {
        LifeContainerState state = LifeContainerState.get(player);
        applyHeartContainers(player, state.getHeartContainers(player.getUuid()));
    }

    public static boolean tryIncreaseHeartContainers(ServerPlayerEntity player) {
        LifeContainerState state = LifeContainerState.get(player);

        if (!state.canIncreaseHeartContainers(player.getUuid())) {
            return false;
        }

        int heartContainers = state.increaseHeartContainers(player.getUuid());
        applyHeartContainers(player, heartContainers);
        player.heal((float) HEALTH_POINTS_PER_HEART);
        return true;
    }

    private static void applyHeartContainers(ServerPlayerEntity player, int heartContainers) {
        double maxHealth = heartContainers * HEALTH_POINTS_PER_HEART;
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);

        if (maxHealthAttribute == null) {
            return;
        }

        maxHealthAttribute.setBaseValue(maxHealth);

        if (player.getHealth() > maxHealth) {
            player.setHealth((float) maxHealth);
        }
    }
}
