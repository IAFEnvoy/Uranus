package com.iafenvoy.uranus.object.entity.pathfinding.raycoms;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */

import com.iafenvoy.uranus.world.WorldChunkUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChunkCache implements LevelReader {
    protected final int chunkX;
    protected final int chunkZ;
    protected final LevelChunk[][] chunkArray;
    /**
     * set by !chunk.getAreLevelsEmpty
     */
    protected final boolean empty;
    /**
     * Reference to the World object.
     */
    protected final Level world;
    /**
     * Dimensiontype.
     */
    private final DimensionType dimType;
    private final int minBuildHeight;
    private final int maxBuildHeight;

    public ChunkCache(Level worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn, final DimensionType type) {
        this.world = worldIn;
        this.chunkX = posFromIn.getX() - subIn >> 4;
        this.chunkZ = posFromIn.getZ() - subIn >> 4;
        int i = posToIn.getX() + subIn >> 4;
        int j = posToIn.getZ() + subIn >> 4;
        this.chunkArray = new LevelChunk[i - this.chunkX + 1][j - this.chunkZ + 1];
        this.empty = true;

        for (int k = this.chunkX; k <= i; ++k)
            for (int l = this.chunkZ; l <= j; ++l)
                if (WorldChunkUtil.isEntityChunkLoaded(this.world, new ChunkPos(k, l)) && worldIn.getChunkSource() instanceof ServerChunkCache serverChunkCache) {
                    final ChunkHolder holder = serverChunkCache.chunkMap.getVisibleChunkIfPresent(ChunkPos.pack(k, l));
                    if (holder != null)
                        this.chunkArray[k - this.chunkX][l - this.chunkZ] = holder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).orElse(null);
                }
        this.dimType = type;
        this.minBuildHeight = worldIn.getMinY();
        this.maxBuildHeight = worldIn.getMaxY();
    }

    /**
     * set by !chunk.getAreLevelsEmpty
     *
     * @return if so.
     */
    public boolean isEmpty() {
        return this.empty;
    }

    @Override
    public BlockEntity getBlockEntity(@NotNull BlockPos pos) {
        return this.getTileEntity(pos, LevelChunk.EntityCreationType.CHECK); // Forge: don't modify world from other threads
    }

    public BlockEntity getTileEntity(BlockPos pos, LevelChunk.EntityCreationType createType) {
        int i = (pos.getX() >> 4) - this.chunkX;
        int j = (pos.getZ() >> 4) - this.chunkZ;
        if (!this.withinBounds(i, j)) return null;
        return this.chunkArray[i][j].getBlockEntity(pos, createType);
    }

    public int getMinBuildHeight() {
        return this.minBuildHeight;
    }

    public int getMaxBuildHeight() {
        return this.maxBuildHeight;
    }

    @NotNull
    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (pos.getY() >= this.getMinBuildHeight() && pos.getY() < this.getMaxBuildHeight()) {
            int i = (pos.getX() >> 4) - this.chunkX;
            int j = (pos.getZ() >> 4) - this.chunkZ;
            if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length) {
                LevelChunk chunk = this.chunkArray[i][j];
                if (chunk != null) return chunk.getBlockState(pos);
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public @NotNull FluidState getFluidState(final BlockPos pos) {
        if (pos.getY() >= this.getMinBuildHeight() && pos.getY() < this.getMaxBuildHeight()) {
            int i = (pos.getX() >> 4) - this.chunkX;
            int j = (pos.getZ() >> 4) - this.chunkZ;
            if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length) {
                LevelChunk chunk = this.chunkArray[i][j];
                if (chunk != null) return chunk.getFluidState(pos);
            }
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public @NotNull Holder<Biome> getUncachedNoiseBiome(final int x, final int y, final int z) {
        return null;
    }

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks material is set to air, meaning it is possible for non-vanilla
     * blocks to still pass this check.
     */
    @Override
    public boolean isEmptyBlock(@NotNull BlockPos pos) {
        BlockState state = this.getBlockState(pos);
        return state.isAir();
    }

    @Override
    public ChunkAccess getChunk(final int x, final int z, final @NotNull ChunkStatus requiredStatus, final boolean nonnull) {
        int i = x - this.chunkX;
        int j = z - this.chunkZ;
        if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length)
            return this.chunkArray[i][j];
        return null;
    }

    @Override
    public boolean hasChunk(final int chunkX, final int chunkZ) {
        return false;
    }

    @Override
    public @NotNull BlockPos getHeightmapPos(final Heightmap.@NotNull Types heightmapType, final @NotNull BlockPos pos) {
        return null;
    }

    @Override
    public int getHeight(final Heightmap.@NotNull Types heightmapType, final int x, final int z) {
        return 0;
    }

    @Override
    public int getSkyDarken() {
        return 0;
    }

    @Override
    public @NotNull BiomeManager getBiomeManager() {
        return null;
    }

    @Override
    public @NotNull WorldBorder getWorldBorder() {
        return null;
    }

    @Override
    public boolean isUnobstructed(final Entity entityIn, final @NotNull VoxelShape shape) {
        return false;
    }

    @Override
    public @NotNull List<VoxelShape> getEntityCollisions(@org.jetbrains.annotations.Nullable final Entity p_186427_, final @NotNull AABB p_186428_) {
        return null;
    }

    @Override
    public int getDirectSignal(@NotNull BlockPos pos, @NotNull Direction direction) {
        return this.getBlockState(pos).getDirectSignal(this, pos, direction);
    }

    @Override
    public @NotNull RegistryAccess registryAccess() {
        return RegistryAccess.EMPTY;
    }

    @Override
    public @NotNull FeatureFlagSet enabledFeatures() {
        return FeatureFlagSet.of();
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public @NotNull DimensionType dimensionType() {
        return this.dimType;
    }

    @Override
    public int getMinY() {
        return this.minBuildHeight;
    }

    @Override
    public int getHeight() {
        return this.maxBuildHeight - this.minBuildHeight;
    }

    @Override
    public net.minecraft.world.attribute.EnvironmentAttributeReader environmentAttributes() {
        return this.world.environmentAttributes();
    }

    private boolean withinBounds(int x, int z) {
        return x >= 0 && x < this.chunkArray.length && z >= 0 && z < this.chunkArray[x].length && this.chunkArray[x][z] != null;
    }

    public float getShade(final @NotNull Direction direction, final boolean b) {
        return 0;
    }

    @Override
    public @NotNull LevelLightEngine getLightEngine() {
        return null;
    }
}
