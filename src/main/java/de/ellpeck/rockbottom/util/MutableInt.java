package de.ellpeck.rockbottom.util;

public class MutableInt{

    private int value;

    public MutableInt(int value){
        this.value = value;
    }

    public MutableInt set(int value){
        this.value = value;
        return this;
    }

    public MutableInt add(int value){
        this.value += value;
        return this;
    }

    public int get(){
        return this.value;
    }
}
