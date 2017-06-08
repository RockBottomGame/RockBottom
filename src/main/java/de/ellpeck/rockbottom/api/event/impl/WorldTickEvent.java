package de.ellpeck.rockbottom.api.event.impl;

import de.ellpeck.rockbottom.api.event.Event;
import de.ellpeck.rockbottom.game.world.World;

public class WorldTickEvent extends Event{

    public final World world;

    public WorldTickEvent(World world){
        this.world = world;
    }
}
