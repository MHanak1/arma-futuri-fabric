package com.mhanak.arma_futuri.block;

import com.mhanak.arma_futuri.blockentity.MirrorBlockEntity;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class MirrorBlock extends Block implements BlockEntityProvider, Waterloggable {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final VoxelShape[] SHAPES = {
            createCuboidShape(0, 0, 0, 16, 16, 2),
            createCuboidShape(14, 0, 0, 16, 16, 16),
            createCuboidShape(0, 0, 14, 16, 16, 16),
            createCuboidShape(0, 0, 0, 2, 16, 16)
    };
    public static final Box[] BOUNDING_BOXES = Arrays.stream(SHAPES).map(VoxelShape::getBoundingBox).toArray(Box[]::new);

    public MirrorBlock(Settings properties) {
        super(properties);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext blockPlaceContext) {
        BlockPos blockPos = blockPlaceContext.getBlockPos();
        FluidState fluidState = blockPlaceContext.getWorld().getFluidState(blockPos);
        Direction face = blockPlaceContext.getSide();
        return this.getDefaultState()
                .with(FACING, face.getAxis() == Direction.Axis.Y ? blockPlaceContext.getHorizontalPlayerFacing().getOpposite() : face)
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    /*
    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.get(WATERLOGGED)) {
            levelAccessor.scheduleFluidTick(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(levelAccessor));
        }

        return super.getStateForNeighborUpdate(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }*/

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MirrorBlockEntity(pos, state);
    }

    /*
    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockGetter, BlockPos blockPos, ShapeContext collisionContext) {
        Direction facing = blockState.get(FACING);
        return SHAPES[facing.getHorizontal()];
    }
*/
    @Override
    public boolean isTransparent(BlockState blockState, BlockView blockGetter, BlockPos blockPos) {
        return blockState.getFluidState().isEmpty();
    }
/*
    @Override
    public float getAmbientOcclusionLightLevel(BlockState blockState, BlockView blockGetter, BlockPos blockPos) {
        return 1.0F;
    }
*/
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }
}
