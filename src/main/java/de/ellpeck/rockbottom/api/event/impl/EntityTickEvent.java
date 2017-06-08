package de.ellpeck.rockbottom.api.event.impl;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.event.Event;

public class EntityTickEvent extends Event{

    public final Entity entity;

    public EntityTickEvent(Entity entity){
        this.entity = entity;
    }
}
