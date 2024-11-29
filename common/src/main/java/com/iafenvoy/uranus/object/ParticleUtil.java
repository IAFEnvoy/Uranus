package com.iafenvoy.uranus.object;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ParticleUtil {
    public static void spawn3x3Particles(World level, ParticleType<DefaultParticleType> particle, double x, double y, double z) {
        spawn3x3Particles(level, (ParticleEffect) particle, x, y, z);
    }

    public static void spawn3x3Particles(World level, ParticleEffect particle, double x, double y, double z) {
        for (double i = -3; i <= 3; i += 3)
            for (double j = -3; j <= 3; j += 3)
                if (i != 0 || j != 0)
                    level.addParticle(particle, x, y + 1.0D, z, i, 0.0D, j);
    }

    public static void spawnCircleParticles(World level, ParticleEffect particle, double x, double y, double z, double r, double angle, double num) {
        if (level instanceof ServerWorld _level)
            for (int index0 = 0; index0 < (int) num; index0++) {
                _level.spawnParticles(particle, x + r * Math.cos(angle), y + 1.0D, z + r * Math.sin(angle), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                angle += 360.0D / num;
            }
    }
}
