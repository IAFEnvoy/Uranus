package com.iafenvoy.uranus.object.entity.pathfinding.raycoms.pathjobs;

public interface ICustomSizeNavigator {
    boolean isSmallerThanBlock();

    float getXZNavSize();

    int getYNavSize();
}
