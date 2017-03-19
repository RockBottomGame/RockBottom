package de.ellpeck.game.data.set.part.num.array;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

public class PartIntIntArray extends BasicDataPart<int[][]>{

    public PartIntIntArray(String name){
        super(name);
    }

    @Override
    public void write(DataOutputStream stream) throws Exception{
        stream.writeInt(this.data.length);

        for(int[] array : this.data){
            stream.writeInt(array.length);

            for(int b : array){
                stream.writeInt(b);
            }
        }
    }

    @Override
    public void read(DataInputStream stream) throws Exception{
        int amount = stream.readInt();
        this.data = new int[amount][];

        for(int i = 0; i < amount; i++){
            int innerAmount = stream.readInt();
            this.data[i] = new int[innerAmount];

            for(int j = 0; j < innerAmount; j++){
                this.data[i][j] = stream.readInt();
            }
        }
    }

    public PartIntIntArray(String name, int[][] data){
        super(name, data);
    }

    @Override
    public String toString(){
        return Arrays.deepToString(this.data);
    }
}
