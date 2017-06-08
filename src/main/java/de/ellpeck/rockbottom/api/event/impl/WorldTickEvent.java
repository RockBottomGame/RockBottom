package de.ellpeck.rockbottom.api.event.impl;

import de.ellpeck.rockbottom.api.event.Event;
import de.ellpeck.rockbottom.api.world.IWorld;

public class WorldTickEvent extends Event{

    public final IWorld world;

    public WorldTickEvent(IWorld world){
        this.world = world;
    }
}
