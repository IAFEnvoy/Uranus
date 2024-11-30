package com.iafenvoy.uranus;

import com.iafenvoy.uranus.util.Timeout;
import dev.architectury.platform.Platform;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Uranus {
    public static final String MOD_ID = "uranus";
    public static final Logger LOGGER = LogManager.getLogger("uranus");

    public static void init() {
        Timeout.startTimeout();

        if (!Platform.isModLoaded("sponsor_core"))
            throw new RuntimeException("Cannot find Sponsor Core, please re-download " + MOD_ID + " or contact author.");
    }
}
