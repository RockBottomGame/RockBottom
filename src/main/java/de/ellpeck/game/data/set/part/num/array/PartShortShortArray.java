package de.ellpeck.game.data.set.part.num.array;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

public class PartShortShortArray extends BasicDataPart<short[][]>{

    public PartShortShortArray(String name){
        super(name);
    }

    @Override
    public void write(DataOutputStream stream) throws Exception{
        stream.writeInt(this.data.length);

        for(short[] array : this.data){
            stream.writeInt(array.length);

            for(int b : array){
                stream.writeShort(b);
            }
        }
    }

    @Override
    public void read(DataInputStream stream) throws Exception{
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

    public PartShortShortArray(String name, short[][] data){
        super(name, data);
    }

    @Override
    public String toString(){
        return Arrays.deepToString(this.data);
    }
}
