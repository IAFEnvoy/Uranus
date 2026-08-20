package com.iafenvoy.uranus.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/** A resource-reloadable texture composed from several source images. */
public class ArrayLayeredTexture extends ReloadableTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    public final List<String> layeredTextureNames;

    public ArrayLayeredTexture(List<String> textureNames) {
        super(textureNames.isEmpty() ? Identifier.withDefaultNamespace("missingno") : Identifier.parse(textureNames.getFirst()));
        this.layeredTextureNames = Collections.unmodifiableList(new ArrayList<>(textureNames));
    }

    @Override
    public TextureContents loadContents(ResourceManager manager) throws IOException {
        if (layeredTextureNames.isEmpty()) return TextureContents.createMissing();
        Iterator<String> iterator = layeredTextureNames.iterator();
        NativeImage base = NativeImage.read(manager.getResourceOrThrow(Identifier.parse(iterator.next())).open());
        try {
            while (iterator.hasNext()) {
                String name = iterator.next();
                if (name == null) continue;
                NativeImage overlay = NativeImage.read(manager.getResourceOrThrow(Identifier.parse(name)).open());
                try {
                    int width = Math.min(base.getWidth(), overlay.getWidth());
                    int height = Math.min(base.getHeight(), overlay.getHeight());
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            base.setPixel(x, y, blend(base.getPixel(x, y), overlay.getPixel(x, y)));
                        }
                    }
                } finally {
                    overlay.close();
                }
            }
            return new TextureContents(base, new net.minecraft.client.resources.metadata.texture.TextureMetadataSection(false, false, net.minecraft.client.renderer.texture.MipmapStrategy.AUTO, 0.1F));
        } catch (Exception exception) {
            base.close();
            LOGGER.error("Couldn't load layered image", exception);
            throw exception;
        }
    }

    private static int blend(int background, int foreground) {
        int foregroundAlpha = foreground >>> 24;
        if (foregroundAlpha == 0) return background;
        if (foregroundAlpha == 255) return foreground;

        int backgroundAlpha = background >>> 24;
        int resultAlpha = foregroundAlpha + backgroundAlpha * (255 - foregroundAlpha) / 255;
        if (resultAlpha == 0) return 0;

        int result = resultAlpha << 24;
        for (int shift = 0; shift <= 16; shift += 8) {
            int foregroundChannel = foreground >> shift & 255;
            int backgroundChannel = background >> shift & 255;
            int channel = (foregroundChannel * foregroundAlpha * 255 + backgroundChannel * backgroundAlpha * (255 - foregroundAlpha)) / (resultAlpha * 255);
            result |= channel << shift;
        }
        return result;
    }
}
