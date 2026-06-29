package net.danskii.wayfarers_tales.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.block.ShapeContext;

public class ShorterGrassBlock extends PlantBlock implements Fertilizable {
    public static final MapCodec<ShorterGrassBlock> CODEC = createCodec(ShorterGrassBlock::new);

    private static final double VISIBLE_TEXTURE_HEIGHT = 9.0D;
    private static final VoxelShape OUTLINE_SHAPE = Block.createColumnShape(12.0D, 0.0D, VISIBLE_TEXTURE_HEIGHT);

    public ShorterGrassBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return Blocks.SHORT_GRASS.getDefaultState().canPlaceAt(world, pos);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos, Blocks.SHORT_GRASS.getDefaultState(), Block.NOTIFY_ALL);
    }
}
