package com.iafenvoy.uranus.object;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class EntityUtil {
    public static <M extends Mob> void summon(EntityType<M> entityType, ServerLevel world, double x, double y, double z) {
            Mob entityToSpawn = entityType.create(world, EntitySpawnReason.COMMAND);
        if (entityToSpawn != null) {
            entityToSpawn.setPos(x, y, z);
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
            entityToSpawn.finalizeSpawn(world, world.getCurrentDifficultyAt(entityToSpawn.blockPosition()), EntitySpawnReason.COMMAND, null);
            world.addFreshEntity(entityToSpawn);
        }
    }

    public static void lightening(ServerLevel world, double x, double y, double z) {
        lightening(world, x, y, z, true);
    }

    public static void lightening(ServerLevel world, double x, double y, double z, boolean cosmetic) {
        LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(world, EntitySpawnReason.TRIGGERED);
        if (entityToSpawn != null) {
            entityToSpawn.setPos(VecUtil.createBottomCenter(x, y, z));
            entityToSpawn.setVisualOnly(cosmetic);
            world.addFreshEntity(entityToSpawn);
        }
    }

    public static void item(ServerLevel world, double x, double y, double z, ItemLike item, int pickUpDelay) {
        item(world, x, y, z, new ItemStack(item), pickUpDelay);
    }

    public static void item(ServerLevel world, double x, double y, double z, ItemStack item, int pickUpDelay) {
        ItemEntity entityToSpawn = new ItemEntity(world, x, y + 1.0d, z, item);
        entityToSpawn.setPickUpDelay(pickUpDelay);
        world.addFreshEntity(entityToSpawn);
    }
}
