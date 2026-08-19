package com.iafenvoy.uranus.client.model;

import com.google.gson.*;
import com.iafenvoy.uranus.client.model.tabula.TabulaModelContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * @author pau101
 * @since 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public enum TabulaModelHandler implements JsonDeserializationContext {
    INSTANCE;

    private final Gson gson = new GsonBuilder().registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer()).registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer()).create();

    public TabulaModelContainer loadTabulaModel(InputStream stream) {
        return this.gson.fromJson(new InputStreamReader(stream), TabulaModelContainer.class);
    }

    @Override
    public <T> T deserialize(JsonElement json, Type type) throws JsonParseException {
        return this.gson.fromJson(json, type);
    }
}