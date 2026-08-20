package com.iafenvoy.uranus.client.render;

import java.util.Optional;

import net.minecraft.resources.Identifier;

public interface EntityWithMarkerTextureProvider extends EntityTextureProvider {
    Optional<Identifier> getMarkerTextureId();
}
