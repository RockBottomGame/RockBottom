package de.ellpeck.game.data.set.part.num.array;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.util.Arrays;

public class PartIntArray extends BasicDataPart<int[]>{

    public PartIntArray(String name){
        super(name);
    }

    public PartIntArray(String name, int[] data){
        super(name, data);
    }

    @Override
    public String write(){
        String s = "";
        for(int data : this.data){
            s += data+";";
        }
        return s.substring(0, s.length()-1);
    }

    @Override
    public void read(String data){
        String[] split = data.split(";");
        this.data = new int[split.length];

        for(int i = 0; i < split.length; i++){
            this.data[i] = Integer.parseInt(split[i]);
        }
    }

    @Override
    public String toString(){
        return Arrays.toString(this.data);
    }
}
