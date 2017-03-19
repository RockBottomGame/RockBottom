package de.ellpeck.game.data.set.part.num.array;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.util.Arrays;

public class PartByteByteArray extends BasicDataPart<byte[][]>{

    public PartByteByteArray(String name){
        super(name);
    }

    public PartByteByteArray(String name, byte[][] data){
        super(name, data);
    }

    @Override
    public String write(){
        String s = "";
        for(byte[] array : this.data){
            for(int data : array){
                s += data+";";
            }
            s+=":";
        }
        return s.substring(0, s.length()-2);
    }

    @Override
    public void read(String data){
        String[] splitRows = data.split(":");
        this.data = new byte[splitRows.length][];

        for(int row = 0; row < splitRows.length; row++){
            String[] splitColumns = splitRows[row].split(";");
            this.data[row] = new byte[splitColumns.length];

            for(int column = 0; column < splitColumns.length; column++){
                this.data[row][column] = Byte.parseByte(splitColumns[column]);
            }
        }
    }

    @Override
    public String toString(){
        return Arrays.deepToString(this.data);
    }
}
