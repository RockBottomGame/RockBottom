package de.ellpeck.game.data.set.part;

public abstract class DataPart<T>{

    protected final String name;

    public DataPart(String name){
        this.name = name;
    }

    public abstract T get();

    public abstract String write();

    public abstract void read(String data);

    public String getName(){
        return this.name;
    }
}
