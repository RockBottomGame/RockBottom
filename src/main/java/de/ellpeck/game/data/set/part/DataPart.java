package de.ellpeck.game.data.set.part;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class DataPart<T>{

    protected final String name;

    public DataPart(String name){
        this.name = name;
    }

    public abstract T get();

    public abstract void write(DataOutputStream stream) throws Exception;

    public abstract void read(DataInputStream stream) throws Exception;

    public String getName(){
        return this.name;
    }
}
