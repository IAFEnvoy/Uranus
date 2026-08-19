package com.iafenvoy.uranus.client.model.tabula;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record TextureOffset(int textureOffsetX, int textureOffsetY) {
}