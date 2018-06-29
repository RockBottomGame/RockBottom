package de.ellpeck.rockbottom.apiimpl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.event.Event;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.event.IEventListener;

import java.util.List;

public class EventHandler implements IEventHandler {

    private final ListMultimap<Class<? extends Event>, IEventListener> registry = ArrayListMultimap.create();

    @Override
    public <T extends Event> void registerListener(Class<T> type, IEventListener<T> listener) {
        List<IEventListener> listeners = this.registry.get(type);
        listeners.add(listener);

        RockBottomAPI.logger().info("Registered event listener " + listener + " for event " + type);
    }

    @Override
    public <T extends Event> void unregisterListener(Class<T> type, IEventListener<T> listener) {
        List<IEventListener> listeners = this.registry.get(type);

        if (listeners != null && listeners.contains(listener)) {
            listeners.remove(listener);
            RockBottomAPI.logger().info("Unregistered event listener " + listener + " for event " + type);
        } else {
            RockBottomAPI.logger().warning("Couldn't unregister event listener " + listener + " for event " + type + " as it wasn't registered");
        }
    }

    @Override
    public void unregisterAllListeners(Class<? extends Event> type) {
        if (this.registry.containsKey(type)) {
            this.registry.removeAll(type);

            RockBottomAPI.logger().info("Unregistered all listeners for event " + type);
        } else {
            RockBottomAPI.logger().warning("Couldn't unregister all events for type " + type + " as there were none registered");
        }
    }

    @Override
    public EventResult fireEvent(Event event) {
        EventResult result = EventResult.DEFAULT;

        List<IEventListener> listeners = this.registry.get(event.getClass());
        if (listeners != null && !listeners.isEmpty()) {
            for (IEventListener listener : listeners) {
                result = listener.listen(result, event);

                if (result == EventResult.CANCELLED) {
                    break;
                }
            }
        }

        return result;
    }
}
