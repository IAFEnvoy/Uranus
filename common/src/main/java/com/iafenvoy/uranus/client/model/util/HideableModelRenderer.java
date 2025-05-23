package com.iafenvoy.uranus.client.model.util;

import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

// The AdvancedModelRenderer/ModelBox uses a child-parent structure
// Meaning that if you change a parents showModel field to false all the children also
// don't get rendered. This is a workaround for that

public class HideableModelRenderer extends AdvancedModelBox {
    public boolean invisible;

    public HideableModelRenderer(AdvancedEntityModel<?> model, String name) {
        super(model, name);
    }

    public HideableModelRenderer(AdvancedEntityModel<?> model, int i, int i1) {
        super(model, i, i1);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer consumer, int light, int overlay, int color) {
        if (this.invisible)
            this.invisibleRender(matrixStack, consumer, light, overlay, color);
        else
            super.render(matrixStack, consumer, light, overlay, color);
    }

    public void copyFrom(BasicModelPart currentModel) {
        this.copyModelAngles(currentModel);
        this.rotationPointX = currentModel.rotationPointX;
        this.rotationPointY = currentModel.rotationPointY;
        this.rotationPointZ = currentModel.rotationPointZ;
    }

    public void invisibleRender(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, int color) {
        if (this.showModel && (!this.cubeList.isEmpty() || !this.childModels.isEmpty())) {
            matrixStackIn.push();
            this.translateAndRotate(matrixStackIn);
            if (!this.scaleChildren)
                matrixStackIn.scale(1.0F / Math.max(this.scaleX, 1.0E-4F), 1.0F / Math.max(this.scaleY, 1.0E-4F), 1.0F / Math.max(this.scaleZ, 1.0E-4F));
            for (BasicModelPart renderer : this.childModels)
                renderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, color);
            matrixStackIn.pop();
        }
    }
}
