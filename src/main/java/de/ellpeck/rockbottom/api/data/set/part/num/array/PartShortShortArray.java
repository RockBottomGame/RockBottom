package de.ellpeck.rockbottom.api.data.set.part.num.array;

import de.ellpeck.rockbottom.api.data.set.part.BasicDataPart;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Arrays;

public class PartShortShortArray extends BasicDataPart<short[][]>{

    public PartShortShortArray(String name){
        super(name);
    }

    public PartShortShortArray(String name, short[][] data){
        super(name, data);
    }

    @Override
    public void write(DataOutput stream) throws Exception{
        stream.writeInt(this.data.length);

        for(short[] array : this.data){
            stream.writeInt(array.length);

            for(int b : array){
                stream.writeShort(b);
            }
        }
    }

    @Override
    public void read(DataInput stream) throws Exception{
        int amount = stream.readInt();
        this.data = new short[amount][];

        for(int i = 0; i < amount; i++){
            int innerAmount = stream.readInt();
            this.data[i] = new short[innerAmount];

            for(int j = 0; j < innerAmount; j++){
                this.data[i][j] = stream.readShort();
            }
        }
    }

    @Override
    public String toString(){
        return Arrays.deepToString(this.data);
    }
}
