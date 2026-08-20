package com.iafenvoy.uranus.object;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

public class PathUtil {
    public static PathType getDanger(PathType type) {
        return type == PathType.DAMAGING || type == PathType.FIRE ? PathType.FIRE_IN_NEIGHBOR :
                type == PathType.DAMAGING_IN_NEIGHBOR ? PathType.DAMAGING_IN_NEIGHBOR :
                        type == PathType.LAVA ? PathType.FIRE :
                                null;
    }

    public static PathType getAiPathNodeType(BlockState state, LevelReader level, BlockPos pos) {
        return state.getBlock() == Blocks.LAVA ? PathType.LAVA : BlockUtil.isBurning(state) ? PathType.FIRE : null;
    }
}
