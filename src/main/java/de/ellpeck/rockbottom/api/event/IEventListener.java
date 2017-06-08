package de.ellpeck.rockbottom.api.event;

public interface IEventListener<T extends Event>{

    EventResult listen(EventResult result, T event);
}
