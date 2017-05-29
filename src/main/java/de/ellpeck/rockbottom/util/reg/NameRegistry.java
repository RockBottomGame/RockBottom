package de.ellpeck.rockbottom.util.reg;

import org.newdawn.slick.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NameRegistry<T>{

    protected final String name;
    protected final Map<String, T> map = new HashMap<>();

    public NameRegistry(String name){
        this.name = name;
    }

    public void register(String name, T value){
        if(name == null || name.isEmpty()){
            throw new IndexOutOfBoundsException("Tried registering "+value+" with name "+name+" which is invalid into registry "+this);
        }
        if(this.map.containsKey(name)){
            throw new RuntimeException("Cannot register "+value+" with name "+name+" twice into registry "+this);
        }

        this.map.put(name, value);

        Log.info("Registered "+value+" with name "+name+" into registry "+this);
    }

    public T get(String name){
        if(name == null || name.isEmpty()){
            Log.warn("Tried getting value of "+name+" for registry "+this+" which is invalid");
            return null;
        }
        else{
            return this.map.get(name);
        }
    }

    public String getName(T value){
        for(Entry<String, T> entry : this.map.entrySet()){
            if(value.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return null;
    }

    public int getSize(){
        return this.map.size();
    }

    public Map<String, T> getUnmodifiable(){
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
