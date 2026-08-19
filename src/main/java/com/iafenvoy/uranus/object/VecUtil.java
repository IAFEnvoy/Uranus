package com.iafenvoy.uranus.object;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class VecUtil {
    public static BlockPos createBlockPos(double x, double y, double z) {
        return new BlockPos((int) x, (int) y, (int) z);
    }

    public static Vec3 createBottomCenter(double x, double y, double z) {
        return Vec3.atBottomCenterOf(createBlockPos(x, y, z));
    }
}
