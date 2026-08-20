package com.iafenvoy.uranus.object.entity.collision;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CustomCollisionsNavigator extends GroundPathNavigation {
    public CustomCollisionsNavigator(Mob mob, Level world) {
        super(mob, world);
    }

    @Override
    protected @NotNull PathFinder createPathFinder(int i) {
        this.nodeEvaluator = new CustomCollisionsNodeProcessor();
        return new PathFinder(this.nodeEvaluator, i);
    }

    protected boolean canMoveDirectly(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
        int i = Mth.floor(posVec31.x);
        int j = Mth.floor(posVec31.z);
        double d0 = posVec32.x - posVec31.x;
        double d1 = posVec32.z - posVec31.z;
        double d2 = d0 * d0 + d1 * d1;
        if (d2 < 1.0E-8D) return false;
        else {
            double d3 = 1.0D / Math.sqrt(d2);
            d0 = d0 * d3;
            d1 = d1 * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;
            if (!this.isSafeToStandAt(i, Mth.floor(posVec31.y), j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                return false;
            } else {
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double) i - posVec31.x;
                double d7 = (double) j - posVec31.z;
                if (d0 >= 0.0D) ++d6;
                if (d1 >= 0.0D) ++d7;

                d6 = d6 / d0;
                d7 = d7 / d1;
                int k = d0 < 0.0D ? -1 : 1;
                int l = d1 < 0.0D ? -1 : 1;
                int i1 = Mth.floor(posVec32.x);
                int j1 = Mth.floor(posVec32.z);
                int k1 = i1 - i;
                int l1 = j1 - j;

                while (k1 * k > 0 || l1 * l > 0) {
                    if (d6 < d7) {
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    } else {
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }
                    if (!this.isSafeToStandAt(i, Mth.floor(posVec31.y), j, sizeX, sizeY, sizeZ, posVec31, d0, d1))
                        return false;
                }
                return true;
            }
        }
    }

    private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3 vec3d, double p_179692_8_, double p_179692_10_) {
        for (BlockPos blockpos : BlockPos.betweenClosed(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
            double d0 = (double) blockpos.getX() + 0.5D - vec3d.x;
            double d1 = (double) blockpos.getZ() + 0.5D - vec3d.z;
            if (d0 * p_179692_8_ + d1 * p_179692_10_ >= 0 && !this.level.getBlockState(blockpos).isPathfindable(PathComputationType.LAND) || ((ICustomCollisions) this.mob).canPassThrough(blockpos, this.level.getBlockState(blockpos), null))
                return false;
        }
        return true;
    }

    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3 vec31, double p_179683_8_, double p_179683_10_) {
        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;
        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_))
            return false;
        else {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for (int k = i; k < i + sizeX; ++k) {
                for (int l = j; l < j + sizeZ; ++l) {
                    double d0 = (double) k + 0.5D - vec31.x;
                    double d1 = (double) l + 0.5D - vec31.z;
                    if (d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0D) {
                        PathType pathnodetype = this.nodeEvaluator.getPathTypeOfMob(new PathfindingContext(this.level, this.mob), k, y - 1, l, this.mob);
                        mutable.set(k, y - 1, l);
                        if (!this.hasValidPathType(pathnodetype) || ((ICustomCollisions) this.mob).canPassThrough(mutable, this.level.getBlockState(mutable), null))
                            return false;

                        pathnodetype = this.nodeEvaluator.getPathTypeOfMob(new PathfindingContext(this.level, this.mob), k, y, l, this.mob);
                        float f = this.mob.getPathfindingMalus(pathnodetype);
                        if (f < 0.0F || f >= 8.0F) return false;

                        if (pathnodetype == PathType.FIRE || pathnodetype == PathType.FIRE_IN_NEIGHBOR || pathnodetype == PathType.DAMAGING)
                            return false;
                    }
                }
            }
            return true;
        }
    }

    @Override
    protected boolean hasValidPathType(@NotNull PathType pathType) {
        if (pathType == PathType.WATER) return false;
        else if (pathType == PathType.LAVA) return false;
        else return pathType != PathType.OPEN;
    }
}
