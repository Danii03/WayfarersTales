package net.danskii.wayfarers_tales.item;

import net.danskii.wayfarers_tales.player.PlayerLifeContainerService;
import net.danskii.wayfarers_tales.registry.ModSoundRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class VesselOfLifeItem extends Item {
    private static final int PARTICLE_COUNT = 42;
    private static final double PARTICLE_OFFSET_XZ = 0.55D;
    private static final double PARTICLE_OFFSET_Y = 0.8D;
    private static final double PARTICLE_SPEED = 0.05D;
    private static final float SUCCESS_SOUND_VOLUME = 1.0F;
    private static final float NOPE_SOUND_VOLUME = 0.75F;
    private static final float SOUND_PITCH = 1.0F;

    public VesselOfLifeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!(user instanceof ServerPlayerEntity player)) {
            return ActionResult.SUCCESS;
        }

        if (!PlayerLifeContainerService.tryIncreaseHeartContainers(player)) {
            playNopeSound(player);
            return ActionResult.SUCCESS_SERVER;
        }

        ItemStack stack = player.getStackInHand(hand);
        stack.decrementUnlessCreative(1, player);
        player.incrementStat(Stats.USED.getOrCreateStat(this));
        spawnLifeParticles(player);
        player.getEntityWorld().playSoundFromEntity(
                null,
                player,
                ModSoundRegistry.LIFE_VESSEL,
                SoundCategory.PLAYERS,
                SUCCESS_SOUND_VOLUME,
                SOUND_PITCH
        );
        return ActionResult.SUCCESS_SERVER;
    }

    private static void spawnLifeParticles(ServerPlayerEntity player) {
        ServerWorld world = player.getEntityWorld();
        world.spawnParticles(
                ParticleTypes.END_ROD,
                player.getX(),
                player.getBodyY(0.6D),
                player.getZ(),
                PARTICLE_COUNT,
                PARTICLE_OFFSET_XZ,
                PARTICLE_OFFSET_Y,
                PARTICLE_OFFSET_XZ,
                PARTICLE_SPEED
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
