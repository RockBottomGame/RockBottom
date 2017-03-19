package de.ellpeck.game.data.set.part.num.array;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

public class PartIntArray extends BasicDataPart<int[]>{

    public PartIntArray(String name){
        super(name);
    }

    @Override
    public void write(DataOutputStream stream) throws Exception{
        stream.writeInt(this.data.length);
        for(int i : this.data){
            stream.writeInt(i);
        }
    }

    @Override
    public void read(DataInputStream stream) throws Exception{
        int amount = stream.readInt();
        this.data = new int[amount];

        for(int i = 0; i < amount; i++){
            this.data[i] = stream.readInt();
        }
    }

    public PartIntArray(String name, int[] data){
        super(name, data);
    }

    @Override
    public String toString(){
        return Arrays.toString(this.data);
    }
}
