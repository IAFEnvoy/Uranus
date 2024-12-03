package com.iafenvoy.uranus.forge;

import com.iafenvoy.uranus.Uranus;
import com.iafenvoy.uranus.client.UranusClient;
import dev.architectury.platform.Platform;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod(Uranus.MOD_ID)
public final class UranusForge {
    public UranusForge() {
        Uranus.init();
        if (Platform.getEnv() == Dist.CLIENT)
            UranusClient.init();
    }
}
