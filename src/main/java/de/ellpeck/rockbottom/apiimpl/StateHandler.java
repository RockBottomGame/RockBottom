package de.ellpeck.rockbottom.apiimpl;

import com.google.common.base.Preconditions;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.IStateHandler;
import de.ellpeck.rockbottom.api.tile.state.TileProp;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.*;

public final class StateHandler implements IStateHandler {

    private final Tile tile;
    private final List<TileProp> properties = new ArrayList<>();

    private TileState defaultState;
    private boolean hasInit;

    public StateHandler(Tile tile) {
        this.tile = tile;
    }

    @Override
    public void init() {
        Preconditions.checkState(!this.hasInit, "Cannot initialize state handler for tile " + this.tile + " twice!");

        Map<String, Comparable> defMap = new TreeMap<>();
        for (TileProp prop : this.getProps()) {
            Comparable def = prop.getDefault();
            int index = prop.getIndex(def);

            Preconditions.checkState(index >= 0 && index < prop.getVariants(), "Default value of property " + prop.getName() + " is not an allowed value! This is not allowed!");

            defMap.put(prop.getName(), def);
        }

        ResourceName defName = InternalHooks.generateTileStateName(this.tile, defMap);
        Preconditions.checkState(this.tile.hasState(defName, defMap), "Tile " + this.tile + " is disallowing its default state from being generated! This is disallowed!");
        this.defaultState = new TileState(defName, this.tile, defMap);

        this.hasInit = true;
    }

    @Override
    public void addProp(TileProp prop) {
        Preconditions.checkState(!this.hasInit, "Cannot add prop " + prop + " to state handler for tile " + this.tile + " after it has been initialized!");
        Preconditions.checkArgument(!this.properties.contains(prop), "Cannot add prop " + prop + " to state handler for tile " + this.tile + " twice!");

        this.properties.add(prop);
    }

    @Override
    public List<TileProp> getProps() {
        return Collections.unmodifiableList(this.properties);
    }

    @Override
    public TileState getDefault() {
        Preconditions.checkState(this.hasInit, "Tried to access default state for tile " + this.tile + " without its state handler being initialized!");
        return this.defaultState;
    }
}
