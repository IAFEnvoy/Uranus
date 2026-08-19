package com.iafenvoy.uranus.object;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUtil {
    public static boolean isLadder(BlockState state) {
        return state.is(BlockTags.CLIMBABLE);
    }

    public static boolean isLadder(Block block) {
        return isLadder(block.defaultBlockState());
    }

    public static boolean isBurning(BlockState block) {
        return isBurning(block.getBlock());
    }

    public static boolean isBurning(Block block) {
        return block == Blocks.FIRE || block == Blocks.LAVA;
    }
}
