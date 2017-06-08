package de.ellpeck.rockbottom.api.event;

public interface IEventHandler{

    <T extends Event> void registerListener(Class<T> type, IEventListener<T> listener);

    <T extends Event> void unregisterListener(Class<T> type, IEventListener<T> listener);

    void unregisterAllListeners(Class<? extends Event> type);

    EventResult fireEvent(Event event);
}
