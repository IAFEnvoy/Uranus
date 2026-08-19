package com.iafenvoy.uranus.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SimpleTexture extends AbstractTexture {
    private final NativeImage nativeImage;

    public SimpleTexture(NativeImage nativeImage) {
        this.nativeImage = nativeImage;
    }

    public void upload(boolean blur, boolean clamp) {
        TextureUtil.prepareImage(this.getId(), 0, this.nativeImage.getWidth(), this.nativeImage.getHeight());
        this.nativeImage.upload(0, 0, 0, 0, 0, this.nativeImage.getWidth(), this.nativeImage.getHeight(), blur, clamp, false, true);
    }

    @Override
    public void load(@NotNull ResourceManager manager) {
    }
}
