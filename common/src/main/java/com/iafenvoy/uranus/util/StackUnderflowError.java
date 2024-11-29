package com.iafenvoy.uranus.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * @author pau101
 * @since 1.0.0
 */
@Environment(EnvType.CLIENT)
public class StackUnderflowError extends Error {
    public StackUnderflowError() {
        super();
    }

    public StackUnderflowError(String s) {
        super(s);
    }
}
