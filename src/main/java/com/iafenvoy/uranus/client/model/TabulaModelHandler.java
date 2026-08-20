package com.iafenvoy.uranus.client.model;

import com.google.gson.*;
import com.iafenvoy.uranus.client.model.tabula.TabulaModelContainer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * @author pau101
 * @since 1.0.0
 */
public enum TabulaModelHandler implements JsonDeserializationContext {
    INSTANCE;

    private final Gson gson = new GsonBuilder().create();

    public TabulaModelContainer loadTabulaModel(InputStream stream) {
        return this.gson.fromJson(new InputStreamReader(stream), TabulaModelContainer.class);
    }

    @Override
    public <T> T deserialize(JsonElement json, Type type) throws JsonParseException {
        return this.gson.fromJson(json, type);
    }
}
