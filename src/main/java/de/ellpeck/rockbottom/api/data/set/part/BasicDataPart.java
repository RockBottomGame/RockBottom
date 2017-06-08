package de.ellpeck.rockbottom.api.data.set.part;

public abstract class BasicDataPart<T> extends DataPart<T>{

    protected T data;

    public BasicDataPart(String name){
        super(name);
    }

    public BasicDataPart(String name, T data){
        this(name);
        this.data = data;
    }

    @Override
    public T get(){
        return this.data;
    }

    @Override
    public String toString(){
        return this.data.toString();
    }
}
