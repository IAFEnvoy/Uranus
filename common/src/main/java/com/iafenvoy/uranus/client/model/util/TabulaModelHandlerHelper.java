package com.iafenvoy.uranus.client.model.util;

import com.iafenvoy.uranus.Uranus;
import com.iafenvoy.uranus.client.model.ITabulaModelAnimator;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.iafenvoy.uranus.client.model.TabulaModelHandler;
import com.iafenvoy.uranus.client.model.tabula.TabulaModelContainer;
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TabulaModelHandlerHelper {
    private static final Map<Identifier, TabulaModelContainer> MODELS = new HashMap<>();

    public static void reloadModel(ResourceManager manager) {
        MODELS.clear();
        for (Map.Entry<Identifier, Resource> entry : manager.findResources("models/tabula", id -> id.getPath().endsWith(".tbl")).entrySet()) {
            Identifier id = entry.getKey();
            try {
                MODELS.put(id, TabulaModelHandler.INSTANCE.loadTabulaModel(getModelJsonStream(id.toString(), entry.getValue().getInputStream())));
            } catch (Exception e) {
                Uranus.LOGGER.error("Failed to load tabula {}", id.toString(), e);
            }
        }
        Uranus.LOGGER.info("Successfully load {} tabula models", MODELS.size());
    }

    @Nullable
    public static TabulaModelContainer getContainer(Identifier id) {
        return MODELS.get(id);
    }

    @Nullable
    public static TabulaModel getModel(Identifier id) {
        return getModel(id, null);
    }

    @Nullable
    public static TabulaModel getModel(Identifier id, Supplier<ITabulaModelAnimator> tabulaAnimator) {
        return getModel(id, new MemorizeSupplier<>(tabulaAnimator));
    }

    @Nullable
    public static TabulaModel getModel(Identifier id, MemorizeSupplier<ITabulaModelAnimator> tabulaAnimator) {
        try {
            String path = "models/tabula/" + id.getPath();
            if (!path.endsWith(".tbl")) path += ".tbl";
            id = id.withPath(path);
            if (MODELS.containsKey(id)) return new TabulaModel(MODELS.get(id), tabulaAnimator);
        } catch (Exception e) {
            Uranus.LOGGER.error("Failed to load model {}", id, e);
        }
        return null;
    }

    @Deprecated(forRemoval = true)
    public static TabulaModelContainer loadTabulaModel(String path) throws IOException {
        if (!path.startsWith("/")) path = "/" + path;
        if (!path.endsWith(".tbl")) path = path + ".tbl";
        InputStream stream = MinecraftClient.getInstance().getResourceManager().open(new Identifier(path));
        return TabulaModelHandler.INSTANCE.loadTabulaModel(getModelJsonStream(path, stream));
    }

    private static InputStream getModelJsonStream(String name, InputStream file) throws IOException {
        ZipInputStream zip = new ZipInputStream(file);
        ZipEntry entry;
        do {
            if ((entry = zip.getNextEntry()) == null)
                throw new RuntimeException("No model.json present in " + name);
        } while (!entry.getName().equals("model.json"));
        return zip;
    }
}
