package com.iafenvoy.uranus;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Uranus.MOD_ID)
@EventBusSubscriber
public final class Uranus {
    public static final String MOD_ID = "uranus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Uranus() {
    }
}
