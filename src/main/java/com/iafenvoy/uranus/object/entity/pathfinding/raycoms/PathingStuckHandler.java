package com.iafenvoy.uranus.object.entity.pathfinding.raycoms;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.Vec3;

/**
 * Stuck handler for pathing
 */
public class PathingStuckHandler implements IStuckHandler {
    /**
     * The distance at which we consider a target to arrive
     */
    private static final double MIN_TARGET_DIST = 3;
    /**
     * Constants related to tp.
     */
    private static final int MIN_TP_DELAY = 120 * 20;
    private static final int MIN_DIST_FOR_TP = 10;
    /**
     * All directions.
     */
    private final List<Direction> directions = Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
    private final Random rand = new Random();
    /**
     * Amount of path steps allowed to teleport on stuck, 0 = disabled
     */
    private int teleportRange = 0;
    /**
     * Max timeout per block to go, default = 5sec per block
     */
    private int timePerBlockDistance = 100;
    /**
     * The current stucklevel, determines actions taken
     */
    private int stuckLevel = 0;
    /**
     * Global timeout counter, used to determine when we're completly stuck
     */
    private int globalTimeout = 0;
    /**
     * The previously desired go to position of the entity
     */
    private BlockPos prevDestination = BlockPos.ZERO;
    /**
     * Whether breaking blocks is enabled
     */
    private boolean canBreakBlocks = false;
    /**
     * Whether placing ladders is enabled
     */
    private boolean canPlaceLadders = false;
    /**
     * Whether leaf bridges are enabled
     */
    private boolean canBuildLeafBridges = false;
    /**
     * Whether teleport to goal at full stuck is enabled
     */
    private boolean canTeleportGoal = false;
    /**
     * Whether take damage on stuck is enabled
     */
    private boolean takeDamageOnCompleteStuck = false;
    private float damagePct = 0.2f;
    /**
     * BLock break range on complete stuck
     */
    private int completeStuckBlockBreakRange = 0;
    /**
     * Temporary comparison variables to compare with last update
     */
    private boolean hadPath = false;
    private int lastPathIndex = -1;
    private int progressedNodes = 0;
    /**
     * Delay before taking unstuck actions in ticks, default 60 seconds
     */
    private int delayBeforeActions = 60 * 20;
    private int delayToNextUnstuckAction = this.delayBeforeActions;
    /**
     * The start position of moving away unstuck
     */
    private BlockPos moveAwayStartPos = BlockPos.ZERO;

    private PathingStuckHandler() {
    }

    /**
     * Creates a new stuck handler
     *
     * @return new stuck handler
     */
    public static PathingStuckHandler createStuckHandler() {
        return new PathingStuckHandler();
    }

    public static Direction getFacing(final BlockPos pos, final BlockPos neighbor) {
        final BlockPos vector = neighbor.subtract(pos);
        return Direction.getNearest(vector.getX(), vector.getY(), -vector.getZ(), Direction.NORTH);
    }

    /**
     * Returns the first air position near the given start. Advances vertically first then horizontally
     *
     * @param start     start position
     * @param vRange    vertical search range
     * @param hRange    horizontal search range
     * @param predicate check predicate for the right block
     * @return position or null
     */
    public static BlockPos findAround(final Level world, final BlockPos start, final int vRange, final int hRange, final BiPredicate<BlockGetter, BlockPos> predicate) {
        if (vRange < 1 && hRange < 1) return null;

        if (predicate.test(world, start)) return start;

        BlockPos temp;
        int y = 0;
        int y_offset = 1;

        for (int i = 0; i < hRange + 2; i++) {
            for (int steps = 1; steps <= vRange; steps++) {
                // Start topleft of middle point
                temp = start.offset(-steps, y, -steps);
                // X ->
                for (int x = 0; x <= steps; x++) {
                    temp = temp.offset(1, 0, 0);
                    if (predicate.test(world, temp))
                        return temp;
                }
                // X
                // |
                // v
                for (int z = 0; z <= steps; z++) {
                    temp = temp.offset(0, 0, 1);
                    if (predicate.test(world, temp))
                        return temp;
                }
                // < - X
                for (int x = 0; x <= steps; x++) {
                    temp = temp.offset(-1, 0, 0);
                    if (predicate.test(world, temp))
                        return temp;
                }
                // ^
                // |
                // X
                for (int z = 0; z <= steps; z++) {
                    temp = temp.offset(0, 0, -1);
                    if (predicate.test(world, temp))
                        return temp;
                }
            }

            y += y_offset;
            y_offset = y_offset > 0 ? y_offset + 1 : y_offset - 1;
            y_offset *= -1;

            if (world.getMaxY() <= start.getY() + y)
                return null;
        }
        return null;
    }

    /**
     * Checks the entity for stuck
     *
     * @param navigator navigator to check
     */
    @Override
    public void checkStuck(final AbstractAdvancedPathNavigate navigator) {
        if (navigator.getDesiredPos() == null || navigator.getDesiredPos().equals(BlockPos.ZERO))
            return;

        final double distanceToGoal = navigator.getOurEntity().position().distanceTo(new Vec3(navigator.getDesiredPos().getX(), navigator.getDesiredPos().getY(), navigator.getDesiredPos().getZ()));

        // Close enough to be considered at the goal
        if (distanceToGoal < MIN_TARGET_DIST) {
            this.resetGlobalStuckTimers();
            return;
        }

        // Global timeout check
        if (this.prevDestination.equals(navigator.getDesiredPos())) {
            this.globalTimeout++;
            // Try path first, if path fits target pos
            if (this.globalTimeout > Math.max(MIN_TP_DELAY, this.timePerBlockDistance * Math.max(MIN_DIST_FOR_TP, distanceToGoal)))
                this.completeStuckAction(navigator);
        } else
            this.resetGlobalStuckTimers();

        this.prevDestination = navigator.getDesiredPos();

        if (navigator.getPath() == null || navigator.getPath().isDone()) {
            // With no path reset the last path index point to -1
            this.lastPathIndex = -1;
            this.progressedNodes = 0;

            // Stuck when we have no path and had no path last update before
            if (!this.hadPath)
                this.tryUnstuck(navigator);
        } else {
            if (navigator.getPath().getNextNodeIndex() == this.lastPathIndex)
                // Stuck when we have a path, but are not progressing on it
                this.tryUnstuck(navigator);
            else {
                if (this.lastPathIndex != -1 && navigator.getPath().getTarget().distSqr(this.prevDestination) < 25) {
                    this.progressedNodes = navigator.getPath().getNextNodeIndex() > this.lastPathIndex ? this.progressedNodes + 1 : this.progressedNodes - 1;
                    if (this.progressedNodes > 5 && (navigator.getPath().getEndNode() == null || !this.moveAwayStartPos.equals(navigator.getPath().getEndNode().asBlockPos())))
                        // Not stuck when progressing
                        this.resetStuckTimers();
                }
            }
        }

        this.lastPathIndex = navigator.getPath() != null ? navigator.getPath().getNextNodeIndex() : -1;

        this.hadPath = navigator.getPath() != null && !navigator.getPath().isDone();
    }

    /**
     * Resets global stuck timers
     */
    private void resetGlobalStuckTimers() {
        this.globalTimeout = 0;
        this.prevDestination = BlockPos.ZERO;
        this.resetStuckTimers();
    }

    /**
     * Final action when completly stuck before resetting stuck handler and path
     */
    private void completeStuckAction(final AbstractAdvancedPathNavigate navigator) {
        final BlockPos desired = navigator.getDesiredPos();
        final Level world = navigator.getOurEntity().level();
        final Mob entity = navigator.getOurEntity();

        if (this.canTeleportGoal) {
            final BlockPos tpPos = findAround(world, desired, 10, 10,
                    (posworld, pos) -> SurfaceType.getSurfaceType(posworld, posworld.getBlockState(pos.below()), pos.below()) == SurfaceType.WALKABLE
                            && SurfaceType.getSurfaceType(posworld, posworld.getBlockState(pos), pos) == SurfaceType.DROPABLE
                            && SurfaceType.getSurfaceType(posworld, posworld.getBlockState(pos.above()), pos.above()) == SurfaceType.DROPABLE);
            if (tpPos != null)
                entity.teleportTo(tpPos.getX() + 0.5, tpPos.getY(), tpPos.getZ() + 0.5);
        }
        if (this.takeDamageOnCompleteStuck)
            entity.hurt(new DamageSource(entity.level().damageSources().inWall().typeHolder(), entity), entity.getMaxHealth() * this.damagePct);

        if (this.completeStuckBlockBreakRange > 0) {
            final Direction facing = getFacing(entity.blockPosition(), navigator.getDesiredPos());

            for (int i = 1; i <= this.completeStuckBlockBreakRange; i++)
                if (!world.isEmptyBlock(new BlockPos(entity.blockPosition()).relative(facing, i)) || !world.isEmptyBlock(new BlockPos(entity.blockPosition()).relative(facing, i).above())) {
                    this.breakBlocksAhead(world, new BlockPos(entity.blockPosition()).relative(facing, i - 1), facing);
                    break;
                }
        }

        navigator.stop();
        this.resetGlobalStuckTimers();
    }

    /**
     * Tries unstuck options depending on the level
     */
    private void tryUnstuck(final AbstractAdvancedPathNavigate navigator) {
        if (this.delayToNextUnstuckAction-- > 0) return;
        this.delayToNextUnstuckAction = 50;

        // Clear path
        if (this.stuckLevel == 0) {
            this.stuckLevel++;
            this.delayToNextUnstuckAction = 100;
            navigator.stop();
            return;
        }

        // Move away
        if (this.stuckLevel == 1) {
            this.stuckLevel++;
            this.delayToNextUnstuckAction = 200;
            navigator.stop();
            navigator.moveAwayFromXYZ(new BlockPos(navigator.getOurEntity().blockPosition()), 10, 1.0f, false);
            navigator.getPathingOptions().setCanClimb(false);
            this.moveAwayStartPos = navigator.getOurEntity().blockPosition();
            return;
        }

        // Skip ahead
        if (this.stuckLevel == 2 && this.teleportRange > 0 && this.hadPath) {
            assert navigator.getPath() != null;
            int index = Math.min(navigator.getPath().getNextNodeIndex() + this.teleportRange, navigator.getPath().getNodeCount() - 1);
            final Node togo = navigator.getPath().getNode(index);
            navigator.getOurEntity().teleportTo(togo.x + 0.5d, togo.y, togo.z + 0.5d);
            this.delayToNextUnstuckAction = 300;
        }

        // Place ladders & leaves
        if (this.stuckLevel >= 3 && this.stuckLevel <= 5)
            if (this.canPlaceLadders && this.rand.nextBoolean()) {
                this.delayToNextUnstuckAction = 200;
                this.placeLadders(navigator);
            } else if (this.canBuildLeafBridges && this.rand.nextBoolean()) {
                this.delayToNextUnstuckAction = 100;
                this.placeLeaves(navigator);
            }

        // break blocks
        if (this.stuckLevel >= 6 && this.stuckLevel <= 8 && this.canBreakBlocks) {
            this.delayToNextUnstuckAction = 200;
            this.breakBlocks(navigator);
        }

        this.chanceStuckLevel();

        if (this.stuckLevel == 9) {
            this.completeStuckAction(navigator);
            this.resetStuckTimers();
        }
    }

    /**
     * Random chance to decrease to a previous level of stuck
     */
    private void chanceStuckLevel() {
        this.stuckLevel++;
        // 20 % to decrease to the previous level again
        if (this.stuckLevel > 1 && this.rand.nextInt(6) == 0)
            this.stuckLevel -= 2;
    }

    /**
     * Resets timers
     */
    private void resetStuckTimers() {
        this.delayToNextUnstuckAction = this.delayBeforeActions;
        this.lastPathIndex = -1;
        this.progressedNodes = 0;
        this.stuckLevel = 0;
        this.moveAwayStartPos = BlockPos.ZERO;
    }

    /**
     * Attempt to break blocks that are blocking the entity to reach its destination.
     *
     * @param world  the world it is in.
     * @param start  the position the entity is at.
     * @param facing the direction the goal is in.
     */
    private void breakBlocksAhead(final Level world, final BlockPos start, final Direction facing) {
        // Above entity
        if (!world.isEmptyBlock(start.above(3))) {
            this.setAirIfPossible(world, start.above(3));
            return;
        }

        // Goal direction up
        if (!world.isEmptyBlock(start.above().relative(facing))) {
            this.setAirIfPossible(world, start.above().relative(facing));
            return;
        }

        // In goal direction
        if (!world.isEmptyBlock(start.relative(facing)))
            this.setAirIfPossible(world, start.relative(facing));
    }

    /**
     * Check if the block at the position is indestructible, if not, attempt to break it.
     *
     * @param world the world the block is in.
     * @param pos   the pos the block is at.
     */
    private void setAirIfPossible(final Level world, final BlockPos pos) {
        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    /**
     * Places ladders
     *
     * @param navigator navigator to use
     */
    private void placeLadders(final AbstractAdvancedPathNavigate navigator) {
        final Level world = navigator.getOurEntity().level();
        final Mob entity = navigator.getOurEntity();

        BlockPos entityPos = entity.blockPosition();

        while (world.getBlockState(entityPos).getBlock() == Blocks.LADDER)
            entityPos = entityPos.above();

        this.tryPlaceLadderAt(world, entityPos);
        this.tryPlaceLadderAt(world, entityPos.above());
        this.tryPlaceLadderAt(world, entityPos.above(2));
    }

    /**
     * Tries to place leaves
     *
     * @param navigator navigator to use
     */
    private void placeLeaves(final AbstractAdvancedPathNavigate navigator) {
        final Level world = navigator.getOurEntity().level();
        final Mob entity = navigator.getOurEntity();

        final Direction badFacing = getFacing(entity.blockPosition(), navigator.getDesiredPos()).getOpposite();

        for (final Direction dir : this.directions) {
            if (dir == badFacing) continue;
            if (world.isEmptyBlock(entity.blockPosition().below().relative(dir)))
                world.setBlockAndUpdate(entity.blockPosition().below().relative(dir), Blocks.ACACIA_LEAVES.defaultBlockState());
        }
    }

    /**
     * Tries to randomly break blocks
     *
     * @param navigator navigator to use
     */
    private void breakBlocks(final AbstractAdvancedPathNavigate navigator) {
        final Level world = navigator.getOurEntity().level();
        final Mob entity = navigator.getOurEntity();

        final Direction facing = getFacing(entity.blockPosition(), navigator.getDesiredPos());

        this.breakBlocksAhead(world, entity.blockPosition(), facing);
    }

    /**
     * Tries to place a ladder at the given position
     *
     * @param world world to use
     * @param pos   position to set
     */
    private void tryPlaceLadderAt(final Level world, final BlockPos pos) {
        final BlockState state = world.getBlockState(pos);
        if (state.getBlock() != Blocks.LADDER && !state.canOcclude() && world.getFluidState(pos).isEmpty())
            for (final Direction dir : this.directions) {
                final BlockState toPlace = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, dir.getOpposite());
                if (world.getBlockState(pos.relative(dir)).isSolid() && toPlace.canSurvive(world, pos)) {
                    world.setBlockAndUpdate(pos, toPlace);
                    break;
                }
            }
    }

    public PathingStuckHandler withBlockBreaks() {
        this.canBreakBlocks = true;
        return this;
    }

    public PathingStuckHandler withPlaceLadders() {
        this.canPlaceLadders = true;
        return this;
    }

    public PathingStuckHandler withBuildLeafBridges() {
        this.canBuildLeafBridges = true;
        return this;
    }

    /**
     * Enables teleporting a certain amount of steps along a generated path
     *
     * @param steps steps to teleport
     * @return this
     */
    public PathingStuckHandler withTeleportSteps(int steps) {
        this.teleportRange = steps;
        return this;
    }

    public PathingStuckHandler withTeleportOnFullStuck() {
        this.canTeleportGoal = true;
        return this;
    }

    public PathingStuckHandler withTakeDamageOnStuck(float damagePct) {
        this.damagePct = damagePct;
        this.takeDamageOnCompleteStuck = true;
        return this;
    }

    /**
     * Sets the time per block distance to travel, before timing out
     *
     * @param time in ticks to set
     * @return this
     */
    public PathingStuckHandler withTimePerBlockDistance(int time) {
        this.timePerBlockDistance = time;
        return this;
    }

    /**
     * Sets the delay before taking stuck actions
     *
     * @param delay to set
     * @return this
     */
    public PathingStuckHandler withDelayBeforeStuckActions(int delay) {
        this.delayBeforeActions = delay;
        return this;
    }

    /**
     * Sets the block break range on complete stuck
     *
     * @param range to set
     * @return this
     */
    public PathingStuckHandler withCompleteStuckBlockBreak(int range) {
        this.completeStuckBlockBreakRange = range;
        return this;
    }
}
