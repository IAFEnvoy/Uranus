package com.iafenvoy.uranus.client.model.tools;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.Entity;

import java.util.List;

public abstract class BasicModelBase<T extends Entity> {
    public int textureWidth = 64;
    public int textureHeight = 32;
    public final List<BasicModelRenderer<T>> boxList = Lists.newArrayList();

    protected BasicModelBase() {
    }

    public void accept(BasicModelRenderer<T> modelRenderer) {
        this.boxList.add(modelRenderer);
    }

    public abstract void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_);

    public void prepareMobModel(T p_102614_, float p_102615_, float p_102616_, float p_102617_) {
    }
}
