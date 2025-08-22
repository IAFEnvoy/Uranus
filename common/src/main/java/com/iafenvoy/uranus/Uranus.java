package com.iafenvoy.uranus;

import com.iafenvoy.uranus.util.Timeout;
import com.mojang.logging.LogUtils;
import dev.architectury.platform.Platform;
import org.slf4j.Logger;

public class Uranus {
    public static final String MOD_ID = "uranus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        Timeout.startTimeout();
    }
}
