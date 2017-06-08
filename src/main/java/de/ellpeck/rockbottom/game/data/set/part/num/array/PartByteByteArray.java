package de.ellpeck.rockbottom.game.data.set.part.num.array;

import de.ellpeck.rockbottom.game.data.set.part.BasicDataPart;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Arrays;

public class PartByteByteArray extends BasicDataPart<byte[][]>{

    public PartByteByteArray(String name){
        super(name);
    }

    public PartByteByteArray(String name, byte[][] data){
        super(name, data);
    }

    @Override
    public void write(DataOutput stream) throws Exception{
        stream.writeInt(this.data.length);

        for(byte[] array : this.data){
            stream.writeInt(array.length);

            for(byte b : array){
                stream.writeByte(b);
            }
        }
    }

    @Override
    public void read(DataInput stream) throws Exception{
        int amount = stream.readInt();
        this.data = new byte[amount][];

        for(int i = 0; i < amount; i++){
            int innerAmount = stream.readInt();
            this.data[i] = new byte[innerAmount];

            for(int j = 0; j < innerAmount; j++){
                this.data[i][j] = stream.readByte();
            }
        }
    }

    @Override
    public String toString(){
        return Arrays.deepToString(this.data);
    }
}
