package com.iafenvoy.uranus.object;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

public class PathUtil {
    public static PathType getDanger(PathType type) {
        return type == PathType.DAMAGE_FIRE || type == PathType.DANGER_FIRE ? PathType.DANGER_FIRE :
                type == PathType.DAMAGE_OTHER || type == PathType.DANGER_OTHER ? PathType.DANGER_OTHER :
                        type == PathType.LAVA ? PathType.DAMAGE_FIRE :
                                null;
    }

    public static PathType getAiPathNodeType(BlockState state, LevelReader level, BlockPos pos) {
        return state.getBlock() == Blocks.LAVA ? PathType.LAVA : BlockUtil.isBurning(state) ? PathType.DAMAGE_FIRE : null;
    }
}
