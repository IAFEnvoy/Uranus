package com.iafenvoy.uranus.object;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class EntityUtil {
    public static <M extends MobEntity> void summon(EntityType<M> entityType, ServerWorld world, double x, double y, double z) {
        MobEntity entityToSpawn = entityType.create(world);
        if (entityToSpawn != null) {
            entityToSpawn.refreshPositionAndAngles(x, y, z, world.getRandom().nextFloat() * 360F, 0);
            entityToSpawn.initialize(world, world.getLocalDifficulty(entityToSpawn.getBlockPos()), SpawnReason.MOB_SUMMONED, null);
            world.spawnEntity(entityToSpawn);
        }
    }

    public static void lightening(ServerWorld world, double x, double y, double z) {
        lightening(world, x, y, z, true);
    }

    public static void lightening(ServerWorld world, double x, double y, double z, boolean cosmetic) {
        LightningEntity entityToSpawn = EntityType.LIGHTNING_BOLT.create(world);
        if (entityToSpawn != null) {
            entityToSpawn.refreshPositionAfterTeleport(VecUtil.createBottomCenter(x, y, z));
            entityToSpawn.setCosmetic(cosmetic);
            world.spawnEntity(entityToSpawn);
        }
    }

    public static void item(ServerWorld world, double x, double y, double z, ItemConvertible item, int pickUpDelay) {
        item(world, x, y, z, new ItemStack(item), pickUpDelay);
    }

    public static void item(ServerWorld world, double x, double y, double z, ItemStack item, int pickUpDelay) {
        ItemEntity entityToSpawn = new ItemEntity(world, x, y + 1.0d, z, item);
        entityToSpawn.setPickupDelay(pickUpDelay);
        world.spawnEntity(entityToSpawn);
    }
}
