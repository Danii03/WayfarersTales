package net.danskii.wayfarers_tales.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public final class ForestFireflyAmbience {
    private static final int CHECK_INTERVAL_TICKS = 80;
    private static final int ACTIVATION_CHANCE = 7;
    private static final int FOREST_RADIUS = 12;
    private static final int FOREST_VERTICAL_RADIUS = 7;
    private static final int MIN_LEAVES = 36;
    private static final int MIN_LOGS = 2;
    private static final int FIREFLY_BURSTS = 3;
    private static final int FIREFLIES_PER_BURST = 5;
    private static final int COLORED_GLOW_PER_BURST = 3;
    private static final int TEMPORARY_LIGHT_TICKS = 70;
    private static final float AMBIENT_SOUND_VOLUME = 0.35F;
    private static final float AMBIENT_SOUND_PITCH = 1.15F;

    private static final DustParticleEffect[] FIREFLY_GLOW_COLORS = {
            new DustParticleEffect(0xFFF38A, 0.8F),
            new DustParticleEffect(0xA8FF9C, 0.75F),
            new DustParticleEffect(0x8FD7FF, 0.7F),
            new DustParticleEffect(0xFFD0F2, 0.65F)
    };

    private static final Map<WorldLightKey, Integer> temporaryLights = new HashMap<>();

    private ForestFireflyAmbience() {
    }

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(ForestFireflyAmbience::tick);
    }

    private static void tick(MinecraftServer server) {
        removeExpiredLights(server);

        if (server.getTicks() % CHECK_INTERVAL_TICKS != 0) {
            return;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerWorld world = player.getEntityWorld();

            if (!World.OVERWORLD.equals(world.getRegistryKey()) || !world.isNight()) {
                continue;
            }

            Random random = world.getRandom();
            if (random.nextInt(ACTIVATION_CHANCE) != 0 || !hasDenseTreesNearby(world, player.getBlockPos())) {
                continue;
            }

            spawnFirefliesNearPlayer(world, player, random);
        }
    }

    private static boolean hasDenseTreesNearby(ServerWorld world, BlockPos center) {
        int leaves = 0;
        int logs = 0;

        for (BlockPos pos : BlockPos.iterate(
                center.add(-FOREST_RADIUS, -FOREST_VERTICAL_RADIUS, -FOREST_RADIUS),
                center.add(FOREST_RADIUS, FOREST_VERTICAL_RADIUS, FOREST_RADIUS)
        )) {
            BlockState state = world.getBlockState(pos);

            if (state.isIn(BlockTags.LEAVES)) {
                leaves++;
            } else if (state.isIn(BlockTags.LOGS)) {
                logs++;
            }

            if (leaves >= MIN_LEAVES && logs >= MIN_LOGS) {
                return true;
            }
        }

        return false;
    }

    private static void spawnFirefliesNearPlayer(ServerWorld world, ServerPlayerEntity player, Random random) {
        for (int burst = 0; burst < FIREFLY_BURSTS; burst++) {
            BlockPos origin = findOpenAirPosition(world, player.getBlockPos(), random);

            if (origin == null) {
                continue;
            }

            world.spawnParticles(
                    ParticleTypes.FIREFLY,
                    origin.getX() + 0.5D,
                    origin.getY() + 0.65D,
                    origin.getZ() + 0.5D,
                    FIREFLIES_PER_BURST,
                    1.8D,
                    0.7D,
                    1.8D,
                    0.015D
            );
            world.spawnParticles(
                    FIREFLY_GLOW_COLORS[random.nextInt(FIREFLY_GLOW_COLORS.length)],
                    origin.getX() + 0.5D,
                    origin.getY() + 0.75D,
                    origin.getZ() + 0.5D,
                    COLORED_GLOW_PER_BURST,
                    1.2D,
                    0.45D,
                    1.2D,
                    0.01D
            );

            if (burst == 0) {
                world.playSound(
                        null,
                        origin.getX() + 0.5D,
                        origin.getY() + 0.5D,
                        origin.getZ() + 0.5D,
                        SoundEvents.BLOCK_FIREFLY_BUSH_IDLE,
                        SoundCategory.AMBIENT,
                        AMBIENT_SOUND_VOLUME,
                        AMBIENT_SOUND_PITCH
                );
            }

            if (random.nextBoolean()) {
                placeTemporaryLight(world, origin, random);
            }
        }
    }

    private static BlockPos findOpenAirPosition(ServerWorld world, BlockPos center, Random random) {
        for (int attempt = 0; attempt < 12; attempt++) {
            BlockPos pos = center.add(
                    random.nextBetween(-8, 8),
                    random.nextBetween(1, 4),
                    random.nextBetween(-8, 8)
            );

            if (world.isAir(pos) && world.isSkyVisibleAllowingSea(pos)) {
                return pos;
            }
        }

        return null;
    }

    private static void placeTemporaryLight(ServerWorld world, BlockPos pos, Random random) {
        if (!world.isAir(pos)) {
            return;
        }

        int lightLevel = random.nextBetween(5, 8);
        world.setBlockState(
                pos,
                Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, lightLevel),
                Block.NOTIFY_LISTENERS
        );
        temporaryLights.put(new WorldLightKey(world.getRegistryKey(), pos.toImmutable()), TEMPORARY_LIGHT_TICKS);
    }

    private static void removeExpiredLights(MinecraftServer server) {
        Iterator<Map.Entry<WorldLightKey, Integer>> iterator = temporaryLights.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<WorldLightKey, Integer> entry = iterator.next();
            int ticksRemaining = entry.getValue() - 1;

            if (ticksRemaining > 0) {
                entry.setValue(ticksRemaining);
                continue;
            }

            ServerWorld world = server.getWorld(entry.getKey().worldKey());
            if (world != null && world.getBlockState(entry.getKey().pos()).isOf(Blocks.LIGHT)) {
                world.setBlockState(entry.getKey().pos(), Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
            }

            iterator.remove();
        }
    }

    private record WorldLightKey(net.minecraft.registry.RegistryKey<World> worldKey, BlockPos pos) {
    }
}
