package de.ellpeck.game.util;

import de.ellpeck.game.Game;
import de.ellpeck.game.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Registry<T>{

    private final Map<Integer, T> map = new HashMap<>();

    public void register(int id, T value){
        if(this.map.containsKey(id)){
            Main.doExceptionInfo(Game.get(), new RuntimeException("Cannot register "+value+" with id "+id+" twice!"));
        }

        this.map.put(id, value);
    }

    public T byId(int id){
        return this.map.get(id);
    }

    public int getId(T value){
        for(Entry<Integer, T> entry : this.map.entrySet()){
            if(value.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return -1;
    }

    public int getSize(){
        return this.map.size();
    }
}
