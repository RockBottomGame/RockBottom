package de.ellpeck.rockbottom.util.reg;

import org.newdawn.slick.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class IndexRegistry<T>{

    protected final int max;
    protected final String name;
    protected final Map<Integer, T> map = new HashMap<>();

    public IndexRegistry(String name, int max){
        this.name = name;
        this.max = max;
    }

    public void register(int id, T value){
        if(id < 0 || id > this.max){
            throw new IndexOutOfBoundsException("Tried registering "+value+" with id "+id+" which is less than 0 or greater than max "+this.max+" in registry "+this);
        }
        if(this.map.containsKey(id)){
            throw new RuntimeException("Cannot register "+value+" with id "+id+" twice into registry "+this);
        }

        this.map.put(id, value);

        Log.info("Registered "+value+" with id "+id+" into registry "+this);
    }

    public T get(int id){
        if(id > this.max){
            Log.warn("Tried getting value of "+id+" for registry "+this+" which is greater than max "+this.max);
            return null;
        }
        else{
            return this.map.get(id);
        }
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

    public Map<Integer, T> getUnmodifiable(){
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
