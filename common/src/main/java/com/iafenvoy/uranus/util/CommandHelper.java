package com.iafenvoy.uranus.util;

import net.minecraft.entity.Entity;

public class CommandHelper {
    public static void execute(Entity entity, String command) {
        execute(entity, command, 4);
    }

    public static void execute(Entity entity, String command, int level) {
        if (!entity.getWorld().isClient() && entity.getServer() != null) {
            entity.getServer().getCommandManager().executeWithPrefix(entity.getCommandSource().withSilent().withLevel(level), command);
        }
    }
}
