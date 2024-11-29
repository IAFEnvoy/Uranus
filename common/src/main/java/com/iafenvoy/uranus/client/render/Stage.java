package com.iafenvoy.uranus.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public enum Stage {
    Zero(0),
    First(1),
    Second(2),
    Third(3),
    Forth(4),
    Fifth(5),
    Sixth(6);
    private final int index;

    Stage(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static Stage getByIndex(int index) {
        for (Stage s : Stage.values())
            if (s.index == index)
                return s;
        return Stage.Zero;
    }

    public static StagedEntityTextureProvider ofProvider(String modId, String... textures) {
        return new StagedEntityTextureProvider(modId, textures);
    }

    public interface StagedEntity {
        Stage getStage();
    }

    public static class StagedEntityTextureProvider {
        private final String modId;
        private final HashMap<Stage, String> textureIds = new HashMap<>();
        private String eyeTextureId = null;

        public StagedEntityTextureProvider(String modId, String... textures) {
            this.modId = modId;
            Stage[] stages = Stage.values();
            for (int i = 0; i < stages.length - 1 && i < textures.length; i++)
                this.textureIds.put(stages[i + 1], textures[i]);
        }

        public StagedEntityTextureProvider setEyeTextureId(String eyeTextureId) {
            this.eyeTextureId = eyeTextureId;
            return this;
        }

        public Identifier getTexture() {
            return this.getTexture(Stage.First);
        }

        public Identifier getTexture(Stage stage) {
            return new Identifier(this.modId, "textures/entities/" + this.getTextureId(stage) + ".png");
        }

        private String getTextureId(Stage stage) {
            if (this.textureIds.containsKey(stage))
                return this.textureIds.get(stage);
            if (this.textureIds.size() == 0)
                return "";
            return this.textureIds.get(Stage.First);
        }

        public <T extends MobEntity> EntityRendererBase<T> createRenderer(EntityRendererFactory.Context context) {
            if (this.eyeTextureId == null)
                return new EntityRendererBase<>(context, this, null);
            return new EntityRendererBase<>(context, this, new Identifier(this.modId, this.eyeTextureId));
        }
    }
}
