package com.iafenvoy.uranus.fabric;

import com.iafenvoy.uranus.Uranus;
import net.fabricmc.api.ModInitializer;

public final class UranusFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Uranus.init();
    }
}
