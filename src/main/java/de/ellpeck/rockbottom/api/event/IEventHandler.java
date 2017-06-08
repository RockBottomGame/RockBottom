package de.ellpeck.rockbottom.api.event;

public interface IEventHandler{

    <T extends Event> void registerListener(Class<T> type, IEventListener<T> listener);

    <T extends Event> void unregisterListener(Class<T> type, IEventListener<T> listener);

    <T extends Event> void unregisterAllListeners(Class<T> type);

    EventResult fireEvent(Event event);
}
