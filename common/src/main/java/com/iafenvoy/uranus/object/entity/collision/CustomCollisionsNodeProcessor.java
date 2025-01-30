package com.iafenvoy.uranus.object.entity.collision;

import com.iafenvoy.uranus.object.PathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathContext;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.Set;

public class CustomCollisionsNodeProcessor extends LandPathNodeMaker {
    public CustomCollisionsNodeProcessor() {
    }

    public static PathNodeType getLandNodeType(PathContext context, BlockPos.Mutable mutable) {
        int i = mutable.getX();
        int j = mutable.getY();
        int k = mutable.getZ();
        PathNodeType pathnodetype = getNodes(context, mutable);
        if (pathnodetype == PathNodeType.OPEN && j >= 1) {
            PathNodeType nodes = getNodes(context, mutable.set(i, j - 1, k));
            pathnodetype = nodes != PathNodeType.WALKABLE && nodes != PathNodeType.OPEN && nodes != PathNodeType.WATER && nodes != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;
            if (nodes == PathNodeType.DAMAGE_FIRE)
                pathnodetype = PathNodeType.DAMAGE_FIRE;
            if (nodes == PathNodeType.DAMAGE_OTHER)
                pathnodetype = PathNodeType.DAMAGE_OTHER;
            if (nodes == PathNodeType.STICKY_HONEY)
                pathnodetype = PathNodeType.STICKY_HONEY;
        }
        if (pathnodetype == PathNodeType.WALKABLE)
            pathnodetype = getNodeTypeFromNeighbors(context, i, j, k, pathnodetype);
        return pathnodetype;
    }


    protected static PathNodeType getNodes(PathContext p_237238_0_, BlockPos p_237238_1_) {
        BlockState blockstate = p_237238_0_.getBlockState(p_237238_1_);
        PathNodeType type = PathUtil.getAiPathNodeType(blockstate, (WorldView) p_237238_0_, p_237238_1_);
        if (type != null) return type;
        if (blockstate.isAir()) return PathNodeType.OPEN;
        else if (blockstate.getBlock() == Blocks.BAMBOO) return PathNodeType.OPEN;
        else return getCommonNodeType(p_237238_0_.getWorld(), p_237238_1_);
    }

    @Override
    public PathNodeType getDefaultNodeType(PathContext context, int x, int y, int z) {
        return getLandNodeType(context, new BlockPos.Mutable(x, y, z));
    }

    @Override
    public Set<PathNodeType> getCollidingNodeTypes(PathContext context, int x, int y, int z) {
        BlockState state = context.getBlockState(context.getEntityPos());
        return ((ICustomCollisions) this.entity).canPassThrough(context.getEntityPos(), state, state.getSidesShape(context.getWorld(), context.getEntityPos())) ? Set.of(PathNodeType.OPEN) : super.getCollidingNodeTypes(context, x, y, z);
    }
}