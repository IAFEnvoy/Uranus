package com.iafenvoy.uranus.object.entity.collision;

import com.iafenvoy.uranus.object.PathUtil;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.NotNull;

public class CustomCollisionsNodeProcessor extends WalkNodeEvaluator {
    public CustomCollisionsNodeProcessor() {
    }

    public static @NotNull PathType getPathTypeStatic(PathfindingContext context, BlockPos.MutableBlockPos mutable) {
        int i = mutable.getX();
        int j = mutable.getY();
        int k = mutable.getZ();
        PathType pathnodetype = getNodes(context, mutable);
        if (pathnodetype == PathType.OPEN && j >= 1) {
            PathType nodes = getNodes(context, mutable.set(i, j - 1, k));
            pathnodetype = nodes != PathType.WALKABLE && nodes != PathType.OPEN && nodes != PathType.WATER && nodes != PathType.LAVA ? PathType.WALKABLE : PathType.OPEN;
            if (nodes == PathType.DAMAGE_FIRE)
                pathnodetype = PathType.DAMAGE_FIRE;
            if (nodes == PathType.DAMAGE_OTHER)
                pathnodetype = PathType.DAMAGE_OTHER;
            if (nodes == PathType.STICKY_HONEY)
                pathnodetype = PathType.STICKY_HONEY;
        }
        if (pathnodetype == PathType.WALKABLE)
            pathnodetype = checkNeighbourBlocks(context, i, j, k, pathnodetype);
        return pathnodetype;
    }


    protected static PathType getNodes(PathfindingContext p_237238_0_, BlockPos p_237238_1_) {
        BlockState blockstate = p_237238_0_.getBlockState(p_237238_1_);
        PathType type = PathUtil.getAiPathNodeType(blockstate, (LevelReader) p_237238_0_, p_237238_1_);
        if (type != null) return type;
        if (blockstate.isAir()) return PathType.OPEN;
        else if (blockstate.getBlock() == Blocks.BAMBOO) return PathType.OPEN;
        else return getPathTypeFromState(p_237238_0_.level(), p_237238_1_);
    }

    @Override
    public @NotNull PathType getPathType(@NotNull PathfindingContext context, int x, int y, int z) {
        return getPathTypeStatic(context, new BlockPos.MutableBlockPos(x, y, z));
    }

    @Override
    public @NotNull Set<PathType> getPathTypeWithinMobBB(PathfindingContext context, int x, int y, int z) {
        BlockState state = context.getBlockState(context.mobPosition());
        return ((ICustomCollisions) this.mob).canPassThrough(context.mobPosition(), state, state.getBlockSupportShape(context.level(), context.mobPosition())) ? Set.of(PathType.OPEN) : super.getPathTypeWithinMobBB(context, x, y, z);
    }
}