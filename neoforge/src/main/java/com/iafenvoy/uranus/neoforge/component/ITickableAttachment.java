package com.iafenvoy.uranus.neoforge.component;

@Deprecated(forRemoval = true)
public interface ITickableAttachment extends IAttachment {
    void tick();

    boolean isDirty();
}
