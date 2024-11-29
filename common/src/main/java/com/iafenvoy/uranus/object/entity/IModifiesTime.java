package com.iafenvoy.uranus.object.entity;

import com.iafenvoy.uranus.server.tick.modifier.TickRateModifier;

public interface IModifiesTime {
    boolean isTimeModificationValid(TickRateModifier modifier);
}
