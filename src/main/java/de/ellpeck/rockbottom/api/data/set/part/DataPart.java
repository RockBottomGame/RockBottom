package de.ellpeck.rockbottom.api.data.set.part;

import java.io.DataInput;
import java.io.DataOutput;

public abstract class DataPart<T>{

    protected final String name;

    public DataPart(String name){
        this.name = name;
    }

    public abstract T get();

    public abstract void write(DataOutput stream) throws Exception;

    public abstract void read(DataInput stream) throws Exception;

    public String getName(){
        return this.name;
    }
}
