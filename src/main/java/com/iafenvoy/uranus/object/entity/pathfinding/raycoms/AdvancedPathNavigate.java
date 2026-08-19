package com.iafenvoy.uranus.object.entity.pathfinding.raycoms;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */

import com.iafenvoy.uranus.Uranus;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.pathjobs.AbstractPathJob;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.pathjobs.PathJobMoveAwayFromLocation;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.pathjobs.PathJobMoveToLocation;
import com.iafenvoy.uranus.object.entity.pathfinding.raycoms.pathjobs.PathJobRandomPos;
import com.iafenvoy.uranus.world.WorldChunkUtil;
import com.iafenvoy.uranus.object.BlockUtil;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.core.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Minecolonies async PathNavigate.
 */
public class AdvancedPathNavigate extends AbstractAdvancedPathNavigate {
    public static final double MIN_Y_DISTANCE = 0.001;
    public static final int MAX_SPEED_ALLOWED = 2;
    public static final double MIN_SPEED_ALLOWED = 0.1;
    /**
     * Spawn pos of minecart.
     */
    private final BlockPos spawnedPos = BlockPos.ZERO;
    public boolean overrideDefaultDimensions;
    private PathResult<AbstractPathJob> pathResult;
    /**
     * Desired position to reach
     */
    private BlockPos desiredPos;
    /**
     * Timeout for the desired pos, resets when its no longer wanted
     */
    private int desiredPosTimeout = 0;
    /**
     * The stuck handler to use
     */
    private IStuckHandler stuckHandler;
    /**
     * Whether we did set sneaking
     */
    private boolean isSneaking = true;
    private double swimSpeedFactor = 1.0;
    private final float width;

    private final float height;

    /**
     * Instantiates the navigation of an ourEntity.
     *
     * @param entity the ourEntity.
     * @param world  the world it is in.
     */
    public AdvancedPathNavigate(final Mob entity, final Level world) {
        this(entity, world, MovementType.WALKING);
    }

    public AdvancedPathNavigate(final Mob entity, final Level world, MovementType type) {
        this(entity, world, type, 1, 1);
        this.overrideDefaultDimensions = false;
    }

    public AdvancedPathNavigate(final Mob entity, final Level world, MovementType type, float width, float height) {
        this(entity, world, type, width, height, PathingStuckHandler.createStuckHandler().withTeleportSteps(6).withTeleportOnFullStuck());
    }

    public AdvancedPathNavigate(final Mob entity, final Level world, MovementType type, float width, float height, PathingStuckHandler stuckHandler) {
        super(entity, world);
        switch (type) {
            case FLYING -> {
                this.nodeEvaluator = new FlyNodeEvaluator();
                this.getPathingOptions().setIsFlying(true);
            }
            case WALKING -> this.nodeEvaluator = new WalkNodeEvaluator();
            case CLIMBING -> {
                this.nodeEvaluator = new WalkNodeEvaluator();
                this.getPathingOptions().setCanClimb(true);
            }
        }
        this.nodeEvaluator.setCanPassDoors(true);
        this.getPathingOptions().setEnterDoors(true);
        this.nodeEvaluator.setCanOpenDoors(true);
        this.getPathingOptions().setCanOpenDoors(true);
        this.nodeEvaluator.setCanFloat(true);
        this.getPathingOptions().setCanSwim(true);
        this.width = width;
        this.height = height;
        this.stuckHandler = stuckHandler;
        this.overrideDefaultDimensions = true;
    }

    /**
     * Similar to WalkNodeProcessor.getGroundY but not broken.
     * This checks if the block below the position we're trying to move to reaches into the block above, if so, it has to aim a little bit higher.
     *
     * @param world the world.
     * @param pos   the position to check.
     * @return the next y level to go to.
     */
    public static double getSmartGroundY(final BlockGetter world, final BlockPos pos) {
        final BlockPos blockpos = pos.below();
        final VoxelShape voxelshape = world.getBlockState(blockpos).getBlockSupportShape(world, blockpos);
        if (voxelshape.isEmpty() || voxelshape.max(Direction.Axis.Y) < 1.0) {
            return pos.getY();
        }
        return blockpos.getY() + voxelshape.max(Direction.Axis.Y);
    }

    public static boolean isEqual(final BlockPos coords, final int x, final int y, final int z) {
        return coords.getX() == x && coords.getY() == y && coords.getZ() == z;
    }

    public static boolean isEntityBlockLoaded(final LevelAccessor world, final BlockPos pos) {
        return WorldChunkUtil.isEntityBlockLoaded(world, pos);
    }

    @Override
    public PathResult moveAwayFromXYZ(final BlockPos avoid, final double range, final double speedFactor, final boolean safeDestination) {
        final BlockPos start = AbstractPathJob.prepareStart(this.ourEntity);

        return this.setPathJob(new PathJobMoveAwayFromLocation(this.ourEntity.level(),
                start,
                avoid,
                (int) range,
                (int) this.ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                this.ourEntity), null, speedFactor, safeDestination);
    }

    @Override
    public PathResult moveToRandomPos(final double range, final double speedFactor) {
        if (this.pathResult != null && this.pathResult.getJob() instanceof PathJobRandomPos) {
            return this.pathResult;
        }

        this.desiredPos = BlockPos.ZERO;
        final int theRange = (int) (this.mob.getRandom().nextInt((int) range) + range / 2);
        final BlockPos start = AbstractPathJob.prepareStart(this.ourEntity);

        return this.setPathJob(new PathJobRandomPos(this.ourEntity.level(),
                start,
                theRange,
                (int) this.ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                this.ourEntity), null, speedFactor, true);
    }

    @Override
    public PathResult moveToRandomPosAroundX(final int range, final double speedFactor, final BlockPos pos) {
        if (this.pathResult != null
                && this.pathResult.getJob() instanceof PathJobRandomPos
                && ((((PathJobRandomPos) this.pathResult.getJob()).posAndRangeMatch(range, pos)))) {
            return this.pathResult;
        }

        this.desiredPos = BlockPos.ZERO;
        return this.setPathJob(new PathJobRandomPos(this.ourEntity.level(),
                AbstractPathJob.prepareStart(this.ourEntity),
                3,
                (int) this.ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                range,
                this.ourEntity, pos), pos, speedFactor, true);
    }

    @Override
    public PathResult moveToRandomPos(final int range, final double speedFactor, final net.minecraft.util.Tuple<BlockPos, BlockPos> corners, final RestrictionType restrictionType) {
        if (this.pathResult != null && this.pathResult.getJob() instanceof PathJobRandomPos) {
            return this.pathResult;
        }

        this.desiredPos = BlockPos.ZERO;
        final int theRange = this.mob.getRandom().nextInt(range) + range / 2;
        final BlockPos start = AbstractPathJob.prepareStart(this.ourEntity);

        return this.setPathJob(new PathJobRandomPos(this.ourEntity.level(),
                start,
                theRange,
                (int) this.ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                this.ourEntity,
                corners.getA(),
                corners.getB(),
                restrictionType), null, speedFactor, true);
    }

    public PathResult setPathJob(
            final AbstractPathJob job,
            final BlockPos dest,
            final double speedFactor, final boolean safeDestination) {
        this.stop();

        this.destination = dest;
        this.originalDestination = dest;
        if (safeDestination) {
            this.desiredPos = dest;
            if (dest != null) {
                this.desiredPosTimeout = 50 * 20;
            }
        }

        this.walkSpeedFactor = speedFactor;

        if (speedFactor > MAX_SPEED_ALLOWED || speedFactor < MIN_SPEED_ALLOWED) {
            Uranus.LOGGER.error("Tried to set a bad speed:{} for entity:{}", speedFactor, this.ourEntity, new Exception());
            return null;
        }

        job.setPathingOptions(this.getPathingOptions());
        this.pathResult = job.getResult();
        this.pathResult.startJob(Pathfinding.getExecutor());
        return this.pathResult;
    }

    @Override
    public boolean isDone() {
        return (this.pathResult == null || this.pathResult.isFinished() && this.pathResult.getStatus() != PathFindingStatus.CALCULATION_COMPLETE) && super.isDone();
    }

    @Override
    public void tick() {
        if (this.overrideDefaultDimensions) {
            this.nodeEvaluator.entityWidth = Mth.floor(this.width + 1.0F);
            this.nodeEvaluator.entityHeight = Mth.floor(this.height + 1.0F);
            this.nodeEvaluator.entityDepth = Mth.floor(this.width + 1.0F);
        } else {
            this.nodeEvaluator.entityWidth = Mth.floor(this.ourEntity.getBbWidth() + 1.0F);
            this.nodeEvaluator.entityHeight = Mth.floor(this.ourEntity.getBbHeight() + 1.0F);
            this.nodeEvaluator.entityDepth = Mth.floor(this.ourEntity.getBbWidth() + 1.0F);
        }
        if (this.desiredPosTimeout > 0) {
            this.desiredPosTimeout--;
        }

        if (this.pathResult != null) {
            if (!this.pathResult.isFinished()) {
                return;
            } else if (this.pathResult.getStatus() == PathFindingStatus.CALCULATION_COMPLETE) {
                this.processCompletedCalculationResult();
            }
        }

        int oldIndex = this.isDone() ? 0 : this.getPath().getNextNodeIndex();

        if (this.isSneaking) {
            this.isSneaking = false;
            this.mob.setShiftKeyDown(false);
        }

        this.ourEntity.setYya(0);
        if (this.handleLadders(oldIndex)) {
            this.followThePath();
            this.stuckHandler.checkStuck(this);
            return;
        }
        if (this.handleRails()) {
            this.stuckHandler.checkStuck(this);
            return;
        }

        // The following block replaces mojangs super.tick(). Why you may ask? Because it's broken, that's why.
        // The moveHelper won't move up if standing in a block with an empty bounding box (put grass, 1 layer snow, mushroom in front of a solid block and have them try jump up).
        ++this.tick;
        if (!this.isDone()) {
            if (this.canUpdatePath()) {
                this.followThePath();
            } else if (this.path != null && !this.path.isDone()) {
                Vec3 vector3d = this.getTempMobPos();
                Vec3 vector3d1 = this.path.getNextEntityPos(this.mob);
                if (vector3d.y > vector3d1.y && !this.mob.onGround() && Mth.floor(vector3d.x) == Mth.floor(vector3d1.x) && Mth.floor(vector3d.z) == Mth.floor(vector3d1.z)) {
                    this.path.advance();
                }
            }

            DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
            if (!this.isDone()) {
                assert this.path != null;
                Vec3 vector3d2 = this.path.getNextEntityPos(this.mob);
                BlockPos blockpos = BlockPos.containing(vector3d2);
                if (isEntityBlockLoaded(this.level, blockpos)) {
                    this.mob.getMoveControl()
                            .setWantedPosition(vector3d2.x,
                                    this.level.getBlockState(blockpos.below()).isAir() ? vector3d2.y : getSmartGroundY(this.level, blockpos),
                                    vector3d2.z,
                                    this.speedModifier);
                }
            }
        }
        // End of super.tick.
        if (this.hasDelayedRecomputation) {
            this.recomputePath();
        }
        if (this.pathResult != null && this.isDone()) {
            this.pathResult.setStatus(PathFindingStatus.COMPLETE);
            this.pathResult = null;
        }
        // Make sure the entity isn't sleeping, tamed or chained when checking if it's stuck
        if (this.mob instanceof TamableAnimal tamableAnimal && tamableAnimal.isInSittingPose()) {
            return;
        }
        if (this.mob instanceof IAdvancedPathingMob advancedPathingMob && advancedPathingMob.stopTickingPathing()) {
            return;
        }

        this.stuckHandler.checkStuck(this);
    }

    @Override
    public PathResult moveToXYZ(final double x, final double y, final double z, final double speedFactor) {
        final int newX = Mth.floor(x);
        final int newY = (int) y;
        final int newZ = Mth.floor(z);

        if (this.pathResult != null && this.pathResult.getJob() instanceof PathJobMoveToLocation &&
                (
                        this.pathResult.isComputing()
                                || (this.destination != null && isEqual(this.destination, newX, newY, newZ))
                                || (this.originalDestination != null && isEqual(this.originalDestination, newX, newY, newZ))
                )
        ) {
            return this.pathResult;
        }

        final BlockPos start = AbstractPathJob.prepareStart(this.ourEntity);
        this.desiredPos = new BlockPos(newX, newY, newZ);

        if (start.subtract(this.desiredPos).getCenter().length() > 1e5) {
            Uranus.LOGGER.warn("Entity {} tried to path to an invalid location {}, {}, {}, if you installed Sable you can ignore this message.", this.ourEntity, newX, newY, newZ);
            return new PathResult();
        }
        return this.setPathJob(
                new PathJobMoveToLocation(this.ourEntity.level(),
                        start,
                        this.desiredPos,
                        (int) this.ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                        this.ourEntity),
                this.desiredPos, speedFactor, true);
    }

    @Override
    public boolean tryMoveToBlockPos(final BlockPos pos, final double speedFactor) {
        this.moveToXYZ(pos.getX(), pos.getY(), pos.getZ(), speedFactor);
        return true;
    }

    //Return a new WalkNodeProcessor for safety reasons eg if the entity
    //has a passenger this method get's called and returning null is not a great idea
    @Override
    protected @NotNull PathFinder createPathFinder(final int p_179679_1_) {
        return new PathFinder(new WalkNodeEvaluator(), p_179679_1_);
    }

    @Override
    protected boolean canUpdatePath() {
        // Auto dismount when trying to path.
        if (this.ourEntity.getVehicle() != null) {
            final PathPointExtended pEx = (PathPointExtended) this.getPath().getNode(this.getPath().getNextNodeIndex());
            if (pEx.isRailsExit()) {
                final Entity entity = this.ourEntity.getVehicle();
                this.ourEntity.stopRiding();
                entity.remove(Entity.RemovalReason.DISCARDED);
            } else if (!pEx.isOnRails()) {
                if (this.destination == null || this.mob.distanceToSqr(this.destination.getX(), this.destination.getY(), this.destination.getZ()) > 2) {
                    this.ourEntity.stopRiding();
                }

            } else if ((Math.abs(pEx.x - this.mob.getX()) > 7 || Math.abs(pEx.z - this.mob.getZ()) > 7) && this.ourEntity.getVehicle() != null) {
                final Entity entity = this.ourEntity.getVehicle();
                this.ourEntity.stopRiding();
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        return true;
    }

    @Override
    protected @NotNull Vec3 getTempMobPos() {
        return this.ourEntity.position();
    }

    @Override
    public @NotNull Path createPath(final @NotNull BlockPos pos, final int accuracy) {
        return null;
    }

    @Override
    protected boolean canMoveDirectly(final @NotNull Vec3 start, final @NotNull Vec3 end) {
        // TODO improve road walking. This is better in some situations, but still not great.
        return super.canMoveDirectly(start, end);
    }

    public double getSpeedFactor() {

        if (this.ourEntity.isInWater()) {
            this.speedModifier = this.walkSpeedFactor * this.swimSpeedFactor;
            return this.speedModifier;
        }

        this.speedModifier = this.walkSpeedFactor;
        return this.walkSpeedFactor;
    }

    @Override
    public void setSpeedModifier(final double speedFactor) {
        if (speedFactor > MAX_SPEED_ALLOWED || speedFactor < MIN_SPEED_ALLOWED) {
            Uranus.LOGGER.debug("Tried to set a bad speed:{} for entity:{}", speedFactor, this.ourEntity);
            return;
        }
        this.walkSpeedFactor = speedFactor;
    }

    /**
     * Deprecated - try to use BlockPos instead
     */
    @Override
    public boolean moveTo(final double x, final double y, final double z, final double speedFactor) {
        if (x == 0 && y == 0 && z == 0) {
            return false;
        }

        this.moveToXYZ(x, y, z, speedFactor);
        return true;
    }

    @Override
    public boolean moveTo(final Entity entityIn, final double speedFactor) {
        return this.tryMoveToBlockPos(entityIn.blockPosition(), speedFactor);
    }

    // Removes stupid vanilla stuff, causing our pathpoints to occasionally be replaced by vanilla ones.
    @Override
    protected void trimPath() {
    }

    @Override
    public boolean moveTo(final Path path, final double speedFactor) {
        if (path == null) {
            this.stop();
            return false;
        }
        /*
          The world time when a path was added.
         */
        long pathStartTime = this.level.getGameTime();
        return super.moveTo(this.convertPath(path), speedFactor);
    }

    /**
     * Converts the given path to a minecolonies path if needed.
     *
     * @param path given path
     * @return resulting path
     */
    private Path convertPath(final Path path) {
        final int pathLength = path.getNodeCount();
        Path tempPath = null;
        if (pathLength > 0 && !(path.getNode(0) instanceof PathPointExtended)) {
            //  Fix vanilla PathPoints to be PathPointExtended
            final PathPointExtended[] newPoints = new PathPointExtended[pathLength];

            for (int i = 0; i < pathLength; ++i) {
                final Node point = path.getNode(i);
                if (!(point instanceof PathPointExtended)) {
                    newPoints[i] = new PathPointExtended(new BlockPos(point.x, point.y, point.z));
                } else {
                    newPoints[i] = (PathPointExtended) point;
                }
            }

            tempPath = new Path(Arrays.asList(newPoints), path.getTarget(), path.canReach());

            final PathPointExtended finalPoint = newPoints[pathLength - 1];
            this.destination = new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z);
        }

        return tempPath == null ? path : tempPath;
    }

    private void processCompletedCalculationResult() {
        this.moveTo(this.pathResult.getPath(), this.getSpeedFactor());

        if (this.pathResult != null)
            this.pathResult.setStatus(PathFindingStatus.IN_PROGRESS_FOLLOWING);
    }

    private boolean handleLadders(int oldIndex) {
        //  Ladder Workaround
        if (!this.isDone()) {
            assert this.getPath() != null;
            final PathPointExtended pEx = (PathPointExtended) this.getPath().getNode(this.getPath().getNextNodeIndex());
            final PathPointExtended pExNext = this.getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 1
                    ? (PathPointExtended) this.getPath().getNode(this.getPath().getNextNodeIndex() + 1) : null;

            final BlockPos pos = new BlockPos(pEx.x, pEx.y, pEx.z);
            if (pEx.isOnLadder() && pExNext != null && (pEx.y != pExNext.y || this.mob.getY() > pEx.y) && BlockUtil.isLadder(this.level.getBlockState(pos)))
                return this.handlePathPointOnLadder(pEx);
            else if (this.ourEntity.isInWater())
                return this.handleEntityInWater(oldIndex, pEx);
            else if (this.level.random.nextInt(10) == 0) {
                if (!pEx.isOnLadder() && pExNext != null && pExNext.isOnLadder())
                    this.speedModifier = this.getSpeedFactor() / 4.0;
                else
                    this.speedModifier = this.getSpeedFactor();
            }
        }
        return false;
    }

    /**
     * Determine what block the entity stands on
     *
     * @param parEntity the entity that stands on the block
     * @return the Blockstate.
     */
    private BlockPos findBlockUnderEntity(final Entity parEntity) {
        int blockX = (int) Math.round(parEntity.getX());
        int blockY = Mth.floor(parEntity.getY() - 0.2D);
        int blockZ = (int) Math.round(parEntity.getZ());
        return new BlockPos(blockX, blockY, blockZ);
    }

    /**
     * Handle rails navigation.
     *
     * @return true if block.
     */
    private boolean handleRails() {
        if (!this.isDone()) {
            assert this.getPath() != null;
            final PathPointExtended pEx = (PathPointExtended) this.getPath().getNode(this.getPath().getNextNodeIndex());
            PathPointExtended pExNext = this.getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 1
                    ? (PathPointExtended) this.getPath().getNode(this.getPath().getNextNodeIndex() + 1) : null;

            if (pExNext != null && pEx.x == pExNext.x && pEx.z == pExNext.z)
                pExNext = this.getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 2
                        ? (PathPointExtended) this.getPath().getNode(this.getPath().getNextNodeIndex() + 2) : null;

            if (pEx.isOnRails() || pEx.isRailsExit())
                return this.handlePathOnRails(pEx, pExNext);
        }
        return false;
    }

    /**
     * Handle pathing on rails.
     *
     * @param pEx     the current path point.
     * @param pExNext the next path point.
     * @return if go to next point.
     */
    private boolean handlePathOnRails(final PathPointExtended pEx, final PathPointExtended pExNext) {
        return false;
    }

    private boolean handlePathPointOnLadder(final PathPointExtended pEx) {
        Vec3 vec3 = this.getPath().getNextEntityPos(this.ourEntity);
        final BlockPos entityPos = new BlockPos(this.ourEntity.blockPosition());
        if (vec3.distanceToSqr(this.ourEntity.getX(), vec3.y, this.ourEntity.getZ()) < 0.6 && Math.abs(vec3.y - entityPos.getY()) <= 2.0) {
            //This way he is less nervous and gets up the ladder
            double newSpeed = 0.3;
            switch (pEx.getLadderFacing()) {
                //  Any of these values is climbing, so adjust our direction of travel towards the ladder
                case NORTH -> vec3 = vec3.add(0, 0, 0.4);
                case SOUTH -> vec3 = vec3.add(0, 0, -0.4);
                case WEST -> vec3 = vec3.add(0.4, 0, 0);
                case EAST -> vec3 = vec3.add(-0.4, 0, 0);
                case UP -> vec3 = vec3.add(0, 1, 0);
                //  Any other value is going down, so lets not move at all
                default -> {
                    newSpeed = 0;
                    this.mob.setShiftKeyDown(true);
                    this.isSneaking = true;
                    this.ourEntity.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, 0.2);
                }
            }

            if (newSpeed > 0) {
                if (!(this.level.getBlockState(this.ourEntity.blockPosition()).getBlock() instanceof LadderBlock))
                    this.ourEntity.setDeltaMovement(this.ourEntity.getDeltaMovement().add(0, 0.1D, 0));
                this.ourEntity.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, newSpeed);
            } else {
                if (BlockUtil.isLadder(this.level.getBlockState(entityPos.below())))
                    this.ourEntity.setYya(-0.5f);
                else return false;
                return true;
            }
        }
        return false;
    }

    private boolean handleEntityInWater(int oldIndex, final PathPointExtended pEx) {
        //  Prevent shortcuts when swimming
        assert this.getPath() != null;
        final int curIndex = this.getPath().getNextNodeIndex();
        if (curIndex > 0 && (curIndex + 1) < this.getPath().getNodeCount() && this.getPath().getNode(curIndex - 1).y != pEx.y)
            //  Work around the initial 'spin back' when dropping into water
            oldIndex = curIndex + 1;

        this.getPath().setNextNodeIndex(oldIndex);

        Vec3 vec3d = this.getPath().getNextEntityPos(this.ourEntity);

        if (vec3d.distanceToSqr(new Vec3(this.ourEntity.getX(), vec3d.y, this.ourEntity.getZ())) < 0.1 && Math.abs(this.ourEntity.getY() - vec3d.y) < 0.5) {
            this.getPath().advance();
            if (this.isDone())
                return true;
            vec3d = this.getPath().getNextEntityPos(this.ourEntity);
        }

        this.ourEntity.getMoveControl().setWantedPosition(vec3d.x, vec3d.y, vec3d.z, this.getSpeedFactor());
        return false;
    }

    @Override
    protected void followThePath() {
        this.getSpeedFactor();
        assert this.path != null;
        final int curNode = this.path.getNextNodeIndex();
        final int curNodeNext = curNode + 1;
        if (curNodeNext < this.path.getNodeCount()) {
            if (!(this.path.getNode(curNode) instanceof PathPointExtended))
                this.path = this.convertPath(this.path);

            final PathPointExtended pEx = (PathPointExtended) this.path.getNode(curNode);
            final PathPointExtended pExNext = (PathPointExtended) this.path.getNode(curNodeNext);

            //  If current node is bottom of a ladder, then stay on this node until
            //  the ourEntity reaches the bottom, otherwise they will try to head out early
            if (pEx.isOnLadder() && pEx.getLadderFacing() == Direction.DOWN && !pExNext.isOnLadder()) {
                final Vec3 vec3 = this.getTempMobPos();
                if ((vec3.y - (double) pEx.y) < MIN_Y_DISTANCE)
                    this.path.setNextNodeIndex(curNodeNext);
                return;
            }
        }

        this.maxDistanceToWaypoint = Math.max(1.2F, this.mob.getBbWidth());
        boolean wentAhead = false;
        boolean isTracking = AbstractPathJob.trackingMap.containsValue(this.ourEntity.getUUID());

        // TODO: Figure out a better way to derive this value ideally from the pathfinding code
        int maxDropHeight = 3;

        // Look at multiple points, incase we're too fast
        for (int i = this.path.getNextNodeIndex(); i < Math.min(this.path.getNodeCount(), this.path.getNextNodeIndex() + 4); i++) {
            Vec3 next = this.path.getEntityPosAtNode(this.mob, i);
            if (Math.abs(this.mob.getX() - next.x) < (double) this.maxDistanceToWaypoint - Math.abs(this.mob.getY() - (next.y)) * 0.1
                    && Math.abs(this.mob.getZ() - next.z) < (double) this.maxDistanceToWaypoint - Math.abs(this.mob.getY() - (next.y)) * 0.1 &&
                    (Math.abs(this.mob.getY() - next.y) <= Math.min(1.0F, Math.ceil(this.mob.getBbHeight() / 2.0F)) ||
                            Math.abs(this.mob.getY() - next.y) <= Math.ceil(this.mob.getBbWidth() / 2) * maxDropHeight)) {
                this.path.advance();
                wentAhead = true;
            }
        }

        if (this.path.isDone()) {
            this.onPathFinish();
            return;
        }

        if (wentAhead) return;
        if (curNode >= this.path.getNodeCount() || curNode <= 1) return;

        // Check some past nodes case we fell behind.
        final Vec3 curr = this.path.getEntityPosAtNode(this.mob, curNode - 1);
        final Vec3 next = this.path.getEntityPosAtNode(this.mob, curNode);

        final Vec3i currI = new Vec3i((int) Math.round(curr.x), (int) Math.round(curr.y), (int) Math.round(curr.z));
        final Vec3i nextI = new Vec3i((int) Math.round(next.x), (int) Math.round(next.y), (int) Math.round(next.z));

        if (this.mob.blockPosition().closerThan(currI, 2.0) && this.mob.blockPosition().closerThan(nextI, 2.0)) {
            int currentIndex = curNode - 1;
            while (currentIndex > 0) {
                final Vec3 tempoPos = this.path.getEntityPosAtNode(this.mob, currentIndex);
                final Vec3i tempoPosI = new Vec3i((int) Math.round(tempoPos.x), (int) Math.round(tempoPos.y), (int) Math.round(tempoPos.z));
                if (this.mob.blockPosition().closerThan(tempoPosI, 1.0))
                    this.path.setNextNodeIndex(currentIndex);
                currentIndex--;
            }
        }
    }

    /**
     * Called upon reaching the path end, reset values
     */
    private void onPathFinish() {
        this.stop();
    }

    @Override
    public void recomputePath() {
    }

    /**
     * Don't let vanilla rapidly discard paths, set a timeout before its allowed to use stuck.
     */
    @Override
    protected void doStuckDetection(final @NotNull Vec3 positionVec3) {
        // Do nothing, unstuck is checked on tick, not just when we have a path
    }

    public boolean entityOnAndBelowPath(Entity entity, Vec3 slack) {
        Path path = this.getPath();
        if (path == null) return false;

        int closest = path.getNextNodeIndex();
        //Search through path from the current index outwards to improve performance
        for (int i = 0; i < path.getNodeCount() - 1; i++) {
            if (closest + i < path.getNodeCount()) {
                Node currentPoint = path.getNode(closest + i);
                if (this.entityNearAndBelowPoint(currentPoint, entity, slack))
                    return true;
            }
            if (closest - i >= 0) {
                Node currentPoint = path.getNode(closest - i);
                if (this.entityNearAndBelowPoint(currentPoint, entity, slack))
                    return true;
            }
        }
        return false;
    }

    private boolean entityNearAndBelowPoint(Node currentPoint, Entity entity, Vec3 slack) {
        return Math.abs(currentPoint.x - entity.getX()) < slack.x()
                && currentPoint.y - entity.getY() + slack.y() > 0
                && Math.abs(currentPoint.z - entity.getZ()) < slack.z();
    }

    @Override
    public void stop() {
        if (this.pathResult != null) {
            this.pathResult.cancel();
            this.pathResult.setStatus(PathFindingStatus.CANCELLED);
            this.pathResult = null;
        }
        this.destination = null;
        super.stop();
    }

    @Override
    public PathResult moveToLivingEntity(final Entity e, final double speed) {
        return this.moveToXYZ(e.getX(), e.getY(), e.getZ(), speed);
    }

    @Override
    public PathResult moveAwayFromLivingEntity(final Entity e, final double distance, final double speed) {
        return this.moveAwayFromXYZ(new BlockPos(e.blockPosition()), distance, speed, true);
    }

    @Override
    public void setCanFloat(boolean canSwim) {
        super.setCanFloat(canSwim);
        this.getPathingOptions().setCanSwim(canSwim);
    }

    @Override
    public BlockPos getDesiredPos() {
        return this.desiredPos;
    }

    /**
     * Sets the stuck handler
     *
     * @param stuckHandler handler to set
     */
    @Override
    public void setStuckHandler(final IStuckHandler stuckHandler) {
        this.stuckHandler = stuckHandler;
    }

    @Override
    public void setSwimSpeedFactor(final double factor) {
        this.swimSpeedFactor = factor;
    }

    public enum MovementType {
        WALKING,
        FLYING,
        CLIMBING
    }
}
