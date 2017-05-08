package de.ellpeck.rockbottom.data.set.part.num.array;

import de.ellpeck.rockbottom.data.set.part.BasicDataPart;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Arrays;

public class PartIntArray extends BasicDataPart<int[]>{

    public PartIntArray(String name){
        super(name);
    }

    public PartIntArray(String name, int[] data){
        super(name, data);
    }

    @Override
    public void write(DataOutput stream) throws Exception{
        stream.writeInt(this.data.length);
        for(int i : this.data){
            stream.writeInt(i);
        }
    }

    @Override
    public void read(DataInput stream) throws Exception{
        int amount = stream.readInt();
        this.data = new int[amount];

        for(int i = 0; i < amount; i++){
            this.data[i] = stream.readInt();
        }
    }

    @Override
    public String toString(){
        return Arrays.toString(this.data);
    }
}
