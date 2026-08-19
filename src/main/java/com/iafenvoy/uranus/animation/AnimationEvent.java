package com.iafenvoy.uranus.animation;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * NeoForge events emitted while an animated entity's animation is updated.
 */
public class AnimationEvent extends Event {
    private final IAnimatedEntity entity;
    private final Animation animation;

    public AnimationEvent(IAnimatedEntity entity, Animation animation) {
        this.entity = entity;
        this.animation = animation;
    }

    public IAnimatedEntity getEntity() {
        return this.entity;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    /**
     * Fired before an animation is synchronized to clients.
     */
    public static final class Start extends AnimationEvent implements ICancellableEvent {
        public Start(IAnimatedEntity entity, Animation animation) {
            super(entity, animation);
        }
    }

    /**
     * Fired after an animation advances by one tick.
     */
    public static final class Tick extends AnimationEvent {
        private final int tick;

        public Tick(IAnimatedEntity entity, Animation animation, int tick) {
            super(entity, animation);
            this.tick = tick;
        }

        public int getTick() {
            return this.tick;
        }
    }
}
