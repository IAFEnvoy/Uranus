package com.iafenvoy.uranus.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Event<T> {
    private final List<T> listeners = new ArrayList<>();
    private final Function<List<T>, T> provider;

    public Event(Function<List<T>, T> provider) {
        this.provider = provider;
    }

    public void register(T listener) {
        this.listeners.add(listener);
    }

    public void unregister(T listener) {
        this.listeners.remove(listener);
    }

    public T invoker() {
        return this.provider.apply(this.listeners);
    }
}
