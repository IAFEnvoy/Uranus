package com.iafenvoy.uranus.client.model.tools;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class BasicModelBase<T extends Entity> extends EntityModel<T> {
    public int textureWidth = 64;
    public int textureHeight = 32;
    public final List<BasicModelRenderer<T>> boxList = Lists.newArrayList();

    protected BasicModelBase() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    protected BasicModelBase(Function<Identifier, RenderLayer> p_102613_) {
        super(p_102613_);
    }

    public void accept(BasicModelRenderer<T> modelRenderer) {
        boxList.add(modelRenderer);
    }

    public abstract void setAngles(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_);

    public void animateModel(T p_102614_, float p_102615_, float p_102616_, float p_102617_) {
    }
}
