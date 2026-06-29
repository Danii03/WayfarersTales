package net.danskii.wayfarers_tales.mixin;

import net.danskii.wayfarers_tales.registry.ModBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrassBlock.class)
public class GrassBlockBonemealMixin {
    private static final int SHORTER_GRASS_BONEMEAL_ATTEMPTS = 32;
    private static final int HORIZONTAL_SPREAD = 3;
    private static final int VERTICAL_SPREAD = 1;

    @Inject(method = "grow", at = @At("TAIL"))
    private void addShorterGrassToBonemeal(ServerWorld world, Random random, BlockPos pos, BlockState state, CallbackInfo ci) {
        for (int attempt = 0; attempt < SHORTER_GRASS_BONEMEAL_ATTEMPTS; attempt++) {
            BlockPos targetPos = pos.add(
                    random.nextBetween(-HORIZONTAL_SPREAD, HORIZONTAL_SPREAD),
                    random.nextBetween(-VERTICAL_SPREAD, VERTICAL_SPREAD) + 1,
                    random.nextBetween(-HORIZONTAL_SPREAD, HORIZONTAL_SPREAD)
            );
            BlockState shorterGrassState = ModBlockRegistry.SHORTER_GRASS.getDefaultState();

            if (world.isAir(targetPos) && shorterGrassState.canPlaceAt(world, targetPos)) {
                world.setBlockState(targetPos, shorterGrassState, Block.NOTIFY_ALL);
            }
        }
    }
}
