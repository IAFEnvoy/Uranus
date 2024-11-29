package com.iafenvoy.uranus.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class LivingEntityEvents {
    public static final Event<Fall> FALL = new com.iafenvoy.uranus.event.Event<>(callbacks -> (entity, fallDistance, multiplier, source) -> {
        for (Fall e : callbacks)
            e.onFall(entity, fallDistance, multiplier, source);
    });

    public static final Event<DamageCallback> DAMAGE = new com.iafenvoy.uranus.event.Event<>(callbacks -> (entity, source, amount) -> {
        for (DamageCallback e : callbacks) {
            float newAmount = e.onLivingDamage(entity, source, amount);
            if (newAmount != amount)
                return newAmount;
        }
        return amount;
    });

    @FunctionalInterface
    public interface Fall {
        void onFall(LivingEntity entity, float fallDistance, float multiplier, DamageSource source);
    }

    @FunctionalInterface
    public interface DamageCallback {
        float onLivingDamage(LivingEntity entity, DamageSource source, float amount);
    }
}
