package com.iafenvoy.uranus.animation;

import com.iafenvoy.uranus.event.Event;

public class AnimationEvents {
    public static final Event<Start> START = new Event<>(callbacks -> (entity, animation) -> {
        for (Start callback : callbacks)
            if (callback.onStart(entity, animation))
                return true;
        return false;
    });
    public static final Event<Tick> TICK = new Event<>(callbacks -> (entity, animation, tick) -> {
        for (Tick callback : callbacks)
            callback.onTick(entity, animation, tick);
    });


    public interface Start {
        //return true to cancel
        boolean onStart(IAnimatedEntity entity, Animation animation);
    }

    public interface Tick {
        void onTick(IAnimatedEntity entity, Animation animation, int tick);
    }
}
