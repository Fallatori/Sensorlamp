package com.sensorlamp.item;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class SensorLanternItem extends StandingAndWallBlockItem {
    public SensorLanternItem(Block lantern, Block wallLantern, Properties properties) {
        super(lantern, wallLantern, properties, Direction.DOWN);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        boolean clickedSide = context.getClickedFace().getAxis().isHorizontal();
        Block preferred = clickedSide ? wallBlock : getBlock();
        Block fallback = clickedSide ? getBlock() : wallBlock;

        for (Block block : new Block[] { preferred, fallback }) {
            BlockState state = block.getStateForPlacement(context);
            if (state != null && canPlace(context.getLevel(), state, context.getClickedPos())
                    && context.getLevel().isUnobstructed(state, context.getClickedPos(), CollisionContext.empty())) {
                return state;
            }
        }
        return null;
    }
}
