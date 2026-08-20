package com.iafenvoy.uranus.object.entity.collision;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.Mth;
import net.minecraft.core.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CustomCollisionsBlockCollisions extends AbstractIterator<VoxelShape> {
    private final AABB box;
    private final CollisionContext context;
    private final Cursor3D cursor;
    private final BlockPos.MutableBlockPos pos;
    private final VoxelShape entityShape;
    private final CollisionGetter collisionGetter;
    private final boolean onlySuffocatingBlocks;
    private BlockGetter cachedBlockGetter;
    private long cachedBlockGetterPos;

    public CustomCollisionsBlockCollisions(CollisionGetter collisionView, Entity entity, AABB box) {
        this(collisionView, entity, box, false);
    }

    public CustomCollisionsBlockCollisions(CollisionGetter collisionView, Entity entity, AABB box, boolean onlySuffocatingBlocks) {
        this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
        this.pos = new BlockPos.MutableBlockPos();
        this.entityShape = Shapes.create(box);
        this.collisionGetter = collisionView;
        this.box = box;
        this.onlySuffocatingBlocks = onlySuffocatingBlocks;
        int i = Mth.floor(box.minX - 1.0E-7D) - 1;
        int j = Mth.floor(box.maxX + 1.0E-7D) + 1;
        int k = Mth.floor(box.minY - 1.0E-7D) - 1;
        int l = Mth.floor(box.maxY + 1.0E-7D) + 1;
        int i1 = Mth.floor(box.minZ - 1.0E-7D) - 1;
        int j1 = Mth.floor(box.maxZ + 1.0E-7D) + 1;
        this.cursor = new Cursor3D(i, k, i1, j, l, j1);
    }

    private BlockGetter getChunk(int p_186412_, int p_186413_) {
        int i = SectionPos.blockToSectionCoord(p_186412_);
        int j = SectionPos.blockToSectionCoord(p_186413_);
        long k = ChunkPos.pack(i, j);
        if (this.cachedBlockGetter != null && this.cachedBlockGetterPos == k)
            return this.cachedBlockGetter;
        else {
            BlockGetter blockView = this.collisionGetter.getChunkForCollisions(i, j);
            this.cachedBlockGetter = blockView;
            this.cachedBlockGetterPos = k;
            return blockView;
        }
    }

    @Override
    protected VoxelShape computeNext() {
        while (true) {
            if (this.cursor.advance()) {
                int i = this.cursor.nextX();
                int j = this.cursor.nextY();
                int k = this.cursor.nextZ();
                int l = this.cursor.getNextType();
                if (l == 3) continue;

                BlockGetter blockgetter = this.getChunk(i, k);
                if (blockgetter == null) continue;

                this.pos.set(i, j, k);
                BlockState blockstate = blockgetter.getBlockState(this.pos);

                if (this.onlySuffocatingBlocks && !blockstate.isSuffocating(blockgetter, this.pos) || l == 1 && !blockstate.hasLargeCollisionShape() || l == 2 && !blockstate.is(Blocks.MOVING_PISTON))
                    continue;

                VoxelShape voxelshape = blockstate.getCollisionShape(this.collisionGetter, this.pos, this.context);
                if (this.context instanceof EntityCollisionContext) {
                    Entity entity = ((EntityCollisionContext) this.context).getEntity();
                    if (entity instanceof ICustomCollisions customCollisions)
                        if (customCollisions.canPassThrough(this.pos, blockstate, voxelshape))
                            continue;
                }
                if (voxelshape == Shapes.block()) {
                    if (!this.box.intersects(i, j, k, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D))
                        continue;
                    return voxelshape.move(i, j, k);
                }

                VoxelShape voxelshape1 = voxelshape.move(i, j, k);
                if (!Shapes.joinIsNotEmpty(voxelshape1, this.entityShape, BooleanOp.AND))
                    continue;

                return voxelshape1;
            }

            return this.endOfData();
        }
    }
}
