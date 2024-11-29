package com.iafenvoy.uranus.object.entity;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class MonsterEntityBase extends HostileEntity {
    private final EntityGroup mobType;

    protected MonsterEntityBase(EntityType<? extends HostileEntity> entityType, World world, EntityGroup mobType) {
        super(entityType, world);
        this.mobType = mobType;
        this.setAiDisabled(false);
    }

    @Override
    public EntityGroup getGroup() {
        return this.mobType;
    }
}
