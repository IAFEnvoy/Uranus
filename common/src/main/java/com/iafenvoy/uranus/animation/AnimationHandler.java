package com.iafenvoy.uranus.animation;

import com.iafenvoy.uranus.ServerHelper;
import com.iafenvoy.uranus.network.AnimationPayload;
import net.minecraft.entity.Entity;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author iLexiconn
 * @since 1.0.0
 */
public enum AnimationHandler {
    INSTANCE;

    /**
     * Sends an animation packet to all clients, notifying them of a changed animation
     *
     * @param entity    the entity with an animation to be updated
     * @param animation the animation to be updated
     * @param <T>       the entity type
     */
    public <T extends Entity & IAnimatedEntity> void sendAnimationMessage(T entity, Animation animation) {
        if (entity.getWorld().isClient) return;
        entity.setAnimation(animation);
        ServerHelper.sendToAll(new AnimationPayload(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation)));
    }

    /**
     * Updates all animations for a given entity
     *
     * @param entity the entity with an animation to be updated
     * @param <T>    the entity type
     */
    public <T extends Entity & IAnimatedEntity> void updateAnimations(T entity) {
        if (entity.getAnimation() == null)
            entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
        else if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            if (entity.getAnimationTick() == 0) {
                if (!AnimationEvents.START.invoker().onStart(entity, entity.getAnimation()))
                    this.sendAnimationMessage(entity, entity.getAnimation());
            }
            if (entity.getAnimationTick() < entity.getAnimation().getDuration()) {
                entity.setAnimationTick(entity.getAnimationTick() + 1);
                AnimationEvents.TICK.invoker().onTick(entity, entity.getAnimation(), entity.getAnimationTick());
            }
            if (entity.getAnimationTick() == entity.getAnimation().getDuration()) {
                entity.setAnimationTick(0);
                entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
            }
        }
    }
}