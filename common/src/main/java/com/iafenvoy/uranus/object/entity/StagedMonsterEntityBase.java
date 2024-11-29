package com.iafenvoy.uranus.object.entity;

import com.iafenvoy.uranus.client.render.Stage;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class StagedMonsterEntityBase extends MonsterEntityBase implements Stage.StagedEntity {
    private Stage stage;

    protected StagedMonsterEntityBase(EntityType<? extends HostileEntity> entityType, World world, EntityGroup mobType, Stage stage) {
        super(entityType, world, mobType);
        this.stage = stage;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("stage"))
            this.stage = Stage.getByIndex(nbt.getInt("stage"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("stage", this.stage.getIndex());
        return nbt;
    }

    @Override
    public Stage getStage() {
        return this.stage;
    }
}
