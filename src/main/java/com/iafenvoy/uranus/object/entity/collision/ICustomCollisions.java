package com.iafenvoy.uranus.object.entity.collision;

import com.google.common.collect.ImmutableList;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface ICustomCollisions {
    /*
        Override Entity#getAllowedMovement with entity method
     */
    static Vec3 getAllowedMovementForEntity(Entity entity, Vec3 vecIN) {
        AABB aabb = entity.getBoundingBox();
        List<VoxelShape> list = entity.level().getEntityCollisions(entity, aabb.expandTowards(vecIN));
        Vec3 vec3 = vecIN.lengthSqr() == 0.0D ? vecIN : collideBoundingBox2(entity, vecIN, aabb, entity.level(), list);
        boolean flag = vecIN.x != vec3.x;
        boolean flag1 = vecIN.y != vec3.y;
        boolean flag2 = vecIN.z != vec3.z;
        boolean flag3 = entity.onGround() || flag1 && vecIN.y < 0.0D;
        if (entity.maxUpStep() > 0.0F && flag3 && (flag || flag2)) {
            Vec3 vec31 = collideBoundingBox2(entity, new Vec3(vecIN.x, entity.maxUpStep(), vecIN.z), aabb, entity.level(), list);
            Vec3 vec32 = collideBoundingBox2(entity, new Vec3(0.0D, entity.maxUpStep(), 0.0D), aabb.expandTowards(vecIN.x, 0.0D, vecIN.z), entity.level(), list);
            if (vec32.y < (double) entity.maxUpStep()) {
                Vec3 vec33 = collideBoundingBox2(entity, new Vec3(vecIN.x, 0.0D, vecIN.z), aabb.move(vec32), entity.level(), list).add(vec32);
                if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr())
                    vec31 = vec33;
            }

            if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr())
                return vec31.add(collideBoundingBox2(entity, new Vec3(0.0D, -vec31.y + vecIN.y, 0.0D), aabb.move(vec31), entity.level(), list));
        }
        return vec3;
    }

    //1.18 logic
    private static Vec3 collideBoundingBox2(Entity entity, Vec3 vec3d, AABB box, Level world, List<VoxelShape> voxelShapes) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builder();
        if (!voxelShapes.isEmpty()) builder.addAll(voxelShapes);

        WorldBorder worldborder = world.getWorldBorder();
        boolean flag = entity != null && worldborder.isInsideCloseToBorder(entity, box.expandTowards(vec3d));
        if (flag) builder.add(worldborder.getCollisionShape());

        builder.addAll(new CustomCollisionsBlockCollisions(world, entity, box.expandTowards(vec3d)));
        return collideWithShapes2(vec3d, box, builder.build());
    }

    private static Vec3 collideWithShapes2(Vec3 vec3d, AABB box, List<VoxelShape> voxelShapes) {
        if (voxelShapes.isEmpty())
            return vec3d;
        else {
            double d0 = vec3d.x;
            double d1 = vec3d.y;
            double d2 = vec3d.z;
            if (d1 != 0.0D) {
                d1 = Shapes.collide(Direction.Axis.Y, box, voxelShapes, d1);
                if (d1 != 0.0D)
                    box = box.move(0.0D, d1, 0.0D);
            }

            boolean flag = Math.abs(d0) < Math.abs(d2);
            if (flag && d2 != 0.0D) {
                d2 = Shapes.collide(Direction.Axis.Z, box, voxelShapes, d2);
                if (d2 != 0.0D)
                    box = box.move(0.0D, 0.0D, d2);
            }

            if (d0 != 0.0D) {
                d0 = Shapes.collide(Direction.Axis.X, box, voxelShapes, d0);
                if (!flag && d0 != 0.0D)
                    box = box.move(d0, 0.0D, 0.0D);
            }

            if (!flag && d2 != 0.0D)
                d2 = Shapes.collide(Direction.Axis.Z, box, voxelShapes, d2);

            return new Vec3(d0, d1, d2);
        }
    }

    boolean canPassThrough(BlockPos mutablePos, BlockState blockstate, VoxelShape voxelshape);
}
