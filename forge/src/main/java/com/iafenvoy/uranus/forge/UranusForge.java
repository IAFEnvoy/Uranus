package com.iafenvoy.uranus.forge;

import com.iafenvoy.uranus.Uranus;
import net.minecraftforge.fml.common.Mod;

@Mod(Uranus.MOD_ID)
public final class UranusForge {
    public UranusForge() {
        Uranus.init();
    }
}
