package com.iafenvoy.uranus.client.model.tools;

import com.google.common.collect.Lists;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public abstract class BasicModelBase<T extends Entity> extends EntityModel<T> {
    public int textureWidth = 64;
    public int textureHeight = 32;
    public final List<BasicModelRenderer<T>> boxList = Lists.newArrayList();

    protected BasicModelBase() {
        this(RenderType::entityCutoutNoCull);
    }

    protected BasicModelBase(Function<ResourceLocation, RenderType> p_102613_) {
        super(p_102613_);
    }

    public void accept(BasicModelRenderer<T> modelRenderer) {
        this.boxList.add(modelRenderer);
    }

    @Override
    public abstract void setupAnim(@NotNull T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_);

    @Override
    public void prepareMobModel(@NotNull T p_102614_, float p_102615_, float p_102616_, float p_102617_) {
    }
}
