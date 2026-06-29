package net.danskii.wayfarers_tales.item;

import net.danskii.wayfarers_tales.player.PlayerLifeContainerService;
import net.danskii.wayfarers_tales.registry.ModSoundRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class VesselOfLifeItem extends Item {
    private static final DustParticleEffect LIFE_GLOW_PARTICLE = new DustParticleEffect(0xFFE78A, 1.15F);
    private static final int GLOW_PARTICLE_COUNT = 42;
    private static final int HEART_PARTICLE_COUNT = 8;
    private static final double GLOW_PARTICLE_OFFSET_XZ = 0.55D;
    private static final double GLOW_PARTICLE_OFFSET_Y = 0.8D;
    private static final double HEART_PARTICLE_OFFSET_XZ = 0.35D;
    private static final double HEART_PARTICLE_OFFSET_Y = 0.45D;
    private static final double GLOW_PARTICLE_SPEED = 0.05D;
    private static final double HEART_PARTICLE_SPEED = 0.02D;
    private static final float SUCCESS_SOUND_VOLUME = 1.0F;
    private static final float NOPE_SOUND_VOLUME = 0.75F;
    private static final float SOUND_PITCH = 1.0F;

    public VesselOfLifeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player && !PlayerLifeContainerService.canIncreaseHeartContainers(player)) {
            playNopeSound(player);
            return ActionResult.SUCCESS_SERVER;
        }

        return super.use(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof ServerPlayerEntity player)) {
            return super.finishUsing(stack, world, user);
        }

        if (!PlayerLifeContainerService.tryIncreaseHeartContainers(player)) {
            playNopeSound(player);
            return stack;
        }

        ItemStack consumedStack = super.finishUsing(stack, world, user);
        spawnLifeParticles(player);
        player.getEntityWorld().playSoundFromEntity(
                null,
                player,
                ModSoundRegistry.LIFE_VESSEL,
                SoundCategory.PLAYERS,
                SUCCESS_SOUND_VOLUME,
                SOUND_PITCH
        );
        return consumedStack;
    }

    private static void spawnLifeParticles(ServerPlayerEntity player) {
        ServerWorld world = player.getEntityWorld();
        world.spawnParticles(
                LIFE_GLOW_PARTICLE,
                player.getX(),
                player.getBodyY(0.6D),
                player.getZ(),
                GLOW_PARTICLE_COUNT,
                GLOW_PARTICLE_OFFSET_XZ,
                GLOW_PARTICLE_OFFSET_Y,
                GLOW_PARTICLE_OFFSET_XZ,
                GLOW_PARTICLE_SPEED
        );
        world.spawnParticles(
                ParticleTypes.HEART,
                player.getX(),
                player.getBodyY(0.85D),
                player.getZ(),
                HEART_PARTICLE_COUNT,
                HEART_PARTICLE_OFFSET_XZ,
                HEART_PARTICLE_OFFSET_Y,
                HEART_PARTICLE_OFFSET_XZ,
                HEART_PARTICLE_SPEED
        );
    }

    private static void playNopeSound(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                Registries.SOUND_EVENT.getEntry(ModSoundRegistry.NOPE_SOUND),
                SoundCategory.PLAYERS,
                player.getX(),
                player.getY(),
                player.getZ(),
                NOPE_SOUND_VOLUME,
                SOUND_PITCH,
                player.getRandom().nextLong()
        ));
    }
}
