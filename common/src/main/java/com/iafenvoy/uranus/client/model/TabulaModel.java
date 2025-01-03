package com.iafenvoy.uranus.client.model;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import com.iafenvoy.uranus.client.model.tabula.TabulaCubeContainer;
import com.iafenvoy.uranus.client.model.tabula.TabulaCubeGroupContainer;
import com.iafenvoy.uranus.client.model.tabula.TabulaModelContainer;
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gegy1000
 * @since 1.0.0
 */
@Environment(EnvType.CLIENT)
public class TabulaModel<T extends Entity> extends AdvancedEntityModel<T> {
    public final ModelAnimator animator;
    protected final Map<String, AdvancedModelBox> cubes = new HashMap<>();
    protected final List<AdvancedModelBox> rootBoxes = new ArrayList<>();
    protected final MemorizeSupplier<ITabulaModelAnimator<T>> tabulaAnimator;
    protected final Map<String, AdvancedModelBox> identifierMap = new HashMap<>();
    protected final double[] scale;

    public TabulaModel(TabulaModelContainer container, MemorizeSupplier<ITabulaModelAnimator<T>> tabulaAnimator) {
        this.texWidth = container.getTextureWidth();
        this.texHeight = container.getTextureHeight();
        this.tabulaAnimator = tabulaAnimator;
        for (TabulaCubeContainer cube : container.getCubes()) {
            this.parseCube(cube, null);
        }
        container.getCubeGroups().forEach(this::parseCubeGroup);
        this.updateDefaultPose();
        this.scale = container.getScale();
        this.animator = ModelAnimator.create();
    }

    public TabulaModel(TabulaModelContainer container) {
        this(container, null);
    }

    private void parseCubeGroup(TabulaCubeGroupContainer container) {
        for (TabulaCubeContainer cube : container.getCubes()) {
            this.parseCube(cube, null);
        }
        container.getCubeGroups().forEach(this::parseCubeGroup);
    }

    private void parseCube(TabulaCubeContainer cube, AdvancedModelBox parent) {
        AdvancedModelBox box = this.createBox(cube);
        this.cubes.put(cube.getName(), box);
        this.identifierMap.put(cube.getIdentifier(), box);
        if (parent != null) {
            parent.addChild(box);
        } else {
            this.rootBoxes.add(box);
        }
        for (TabulaCubeContainer child : cube.getChildren()) {
            this.parseCube(child, box);
        }
    }

    private AdvancedModelBox createBox(TabulaCubeContainer cube) {
        int[] textureOffset = cube.getTextureOffset();
        double[] position = cube.getPosition();
        double[] rotation = cube.getRotation();
        double[] offset = cube.getOffset();
        int[] dimensions = cube.getDimensions();
        float scaleIn = 0;
        AdvancedModelBox box = new AdvancedModelBox(this, cube.getName());
        box.setTextureOffset(textureOffset[0], textureOffset[1]);
        box.mirror = cube.isTextureMirrorEnabled();
        box.setPos((float) position[0], (float) position[1], (float) position[2]);
        box.addBox((float) offset[0], (float) offset[1], (float) offset[2], dimensions[0], dimensions[1], dimensions[2], scaleIn);
        box.rotateAngleX = (float) Math.toRadians(rotation[0]);
        box.rotateAngleY = (float) Math.toRadians(rotation[1]);
        box.rotateAngleZ = (float) Math.toRadians(rotation[2]);
        return box;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        ITabulaModelAnimator<T> tabulaModelAnimator = this.tabulaAnimator.get();
        if (tabulaModelAnimator != null)
            tabulaModelAnimator.setRotationAngles(this, entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 1.0F);
    }

    public AdvancedModelBox getCube(String name) {
        return this.cubes.get(name);
    }

    public Map<String, AdvancedModelBox> getCubes() {
        return this.cubes;
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.copyOf(this.rootBoxes);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.copyOf(this.cubes.values());
    }

    public List<AdvancedModelBox> getRootBox() {
        return this.rootBoxes;
    }
}