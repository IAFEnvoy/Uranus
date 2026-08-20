package com.iafenvoy.uranus.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

/** Small wrapper retaining the old constructor while using the 26.1 GPU texture path. */
public class SimpleTexture extends DynamicTexture {
    public SimpleTexture(NativeImage nativeImage) {
        super(() -> "uranus_dynamic", nativeImage);
    }

    public void upload(boolean blur, boolean clamp) {
        upload();
    }
}
