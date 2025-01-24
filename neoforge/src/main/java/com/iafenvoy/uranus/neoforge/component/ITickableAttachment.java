package com.iafenvoy.uranus.neoforge.component;

public interface ITickableAttachment extends IAttachment {
    void tick();

    boolean isDirty();
}
