package com.iafenvoy.uranus.client.model.util;

import com.iafenvoy.uranus.Uranus;
import com.iafenvoy.uranus.client.model.ITabulaModelAnimator;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.iafenvoy.uranus.client.model.TabulaModelHandler;
import com.iafenvoy.uranus.client.model.tabula.TabulaModelContainer;
import com.iafenvoy.uranus.util.function.MemorizeSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;

public class TabulaModelHandlerHelper {
    private static final Map<ResourceLocation, TabulaModelContainer> MODELS = new HashMap<>();

    public static void reloadModel(ResourceManager manager) {
        MODELS.clear();
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("models/tabula", id -> id.getPath().endsWith(".tbl")).entrySet()) {
            ResourceLocation id = entry.getKey();
            try {
                MODELS.put(id, TabulaModelHandler.INSTANCE.loadTabulaModel(getModelJsonStream(id.toString(), entry.getValue().open())));
            } catch (Exception e) {
                Uranus.LOGGER.error("Failed to load tabula {}", id.toString(), e);
            }
        }
        Uranus.LOGGER.info("Successfully load {} tabula models", MODELS.size());
    }

    @Nullable
    public static TabulaModelContainer getContainer(ResourceLocation id) {
        return MODELS.get(id);
    }

    @Nullable
    public static <T extends Entity> TabulaModel<T> getModel(ResourceLocation id) {
        return getModel(id, null);
    }

    @Nullable
    public static <T extends Entity> TabulaModel<T> getModel(ResourceLocation id, Supplier<ITabulaModelAnimator<T>> tabulaAnimator) {
        return getModel(id, new MemorizeSupplier<>(tabulaAnimator));
    }

    @Nullable
    public static <T extends Entity> TabulaModel<T> getModel(ResourceLocation id, MemorizeSupplier<ITabulaModelAnimator<T>> tabulaAnimator) {
        try {
            String path = "models/tabula/" + id.getPath();
            if (!path.endsWith(".tbl")) path += ".tbl";
            id = id.withPath(path);
            if (MODELS.containsKey(id)) return new TabulaModel<>(MODELS.get(id), tabulaAnimator);
        } catch (Exception e) {
            Uranus.LOGGER.error("Failed to load model {}", id, e);
        }
        return null;
    }

    @Deprecated(forRemoval = true)
    public static TabulaModelContainer loadTabulaModel(String path) throws IOException {
        if (!path.startsWith("/")) path = "/" + path;
        if (!path.endsWith(".tbl")) path = path + ".tbl";
        InputStream stream = Minecraft.getInstance().getResourceManager().open(ResourceLocation.parse(path));
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
