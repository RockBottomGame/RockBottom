package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.IStateHandler;
import de.ellpeck.rockbottom.api.tile.state.TileProp;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.*;

public final class StateHandler implements IStateHandler{

    private final Tile tile;
    private final List<TileProp> properties = new ArrayList<>();

    private TileState defaultState;
    private boolean hasInit;

    public StateHandler(Tile tile){
        this.tile = tile;
    }

    @Override
    public void init(){
        if(!this.hasInit){
            this.hasInit = true;

            Map<String, Comparable> defMap = new TreeMap<>();
            for(TileProp prop : this.getProps()){
                Comparable def = prop.getDefault();
                int index = prop.getIndex(def);

                if(index >= 0 && index < prop.getVariants()){
                    defMap.put(prop.getName(), def);
                }
                else{
                    throw new IllegalArgumentException();
                }
            }

            IResourceName defName = InternalHooks.generateTileStateName(this.tile, defMap);
            if(this.tile.hasState(defName, defMap)){
                this.defaultState = new TileState(defName, this.tile, defMap);
            }
            else{
                throw new RuntimeException("Tile "+this.tile+" is disallowing its default state from being generated! This is disallowed!");
            }
        }
        else{
            throw new RuntimeException("Cannot initialize state handler for tile "+this.tile+" twice!");
        }
    }

    @Override
    public void addProp(TileProp prop){
        if(!this.hasInit){
            if(!this.properties.contains(prop)){
                this.properties.add(prop);
            }
            else{
                throw new IllegalArgumentException("Cannot add prop "+prop+" to state handler for tile "+this.tile+" twice!");
            }
        }
        else{
            throw new RuntimeException("Cannot add prop "+prop+" to state handler for tile "+this.tile+" after it has been initialized!");
        }
    }

    @Override
    public List<TileProp> getProps(){
        return Collections.unmodifiableList(this.properties);
    }

    @Override
    public TileState getDefault(){
        if(!this.hasInit){
            throw new RuntimeException("Tried to access default state for tile "+this.tile+" without its state handler being initialized!");
        }

        return this.defaultState;
    }
}
