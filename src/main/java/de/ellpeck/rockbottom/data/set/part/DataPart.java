package de.ellpeck.rockbottom.data.set.part;

import java.io.*;

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
