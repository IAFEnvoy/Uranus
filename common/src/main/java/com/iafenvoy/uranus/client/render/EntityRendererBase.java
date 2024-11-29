package com.iafenvoy.uranus.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class EntityRendererBase<T extends MobEntity> extends BipedEntityRenderer<T, BipedEntityModel<T>> {
    private final Stage.StagedEntityTextureProvider textureId;

    public EntityRendererBase(EntityRendererFactory.Context context, Stage.StagedEntityTextureProvider textureId, @Nullable Identifier eyeTextureId) {
        super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER)), 0.5F);
        this.textureId = textureId;
        this.addFeature(new ArmorFeatureRenderer<>(this, new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)), new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        if (eyeTextureId != null)
            this.addFeature(new EyesFeatureRenderer<>(this) {
                public RenderLayer getEyesTexture() {
                    return RenderLayer.getEyes(eyeTextureId);
                }
            });
    }

    @Override
    public Identifier getTexture(T entity) {
        if (entity instanceof Stage.StagedEntity stagedEntity)
            return this.textureId.getTexture(stagedEntity.getStage());
        return this.textureId.getTexture();
    }
}
