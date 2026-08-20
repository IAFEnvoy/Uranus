package com.iafenvoy.uranus.client.model.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.NonNull;

/** Render-state equivalent of the legacy delegating, hideable entity layer. */
public class HideableLayer<S extends EntityRenderState, M extends EntityModel<? super S>, C extends RenderLayer<S, M>> extends RenderLayer<S, M> {
    private final C layerRenderer;
    public boolean hidden;

    public HideableLayer(C layerRenderer, RenderLayerParent<S, M> entityRenderer) {
        super(entityRenderer);
        this.layerRenderer = layerRenderer;
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, int packedLight, S state, float yRot, float partialTick) {
        if (!this.hidden) this.layerRenderer.submit(poseStack, collector, packedLight, state, yRot, partialTick);
    }
}
