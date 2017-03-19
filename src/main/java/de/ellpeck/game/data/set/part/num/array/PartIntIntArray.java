package de.ellpeck.game.data.set.part.num.array;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.util.Arrays;

public class PartIntIntArray extends BasicDataPart<int[][]>{

    public PartIntIntArray(String name){
        super(name);
    }

    public PartIntIntArray(String name, int[][] data){
        super(name, data);
    }

    @Override
    public String write(){
        String s = "";
        for(int[] array : this.data){
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
        this.data = new int[splitRows.length][];

        for(int row = 0; row < splitRows.length; row++){
            String[] splitColumns = splitRows[row].split(";");
            this.data[row] = new int[splitColumns.length];

            for(int column = 0; column < splitColumns.length; column++){
                this.data[row][column] = Integer.parseInt(splitColumns[column]);
            }
        }
    }

    @Override
    public String toString(){
        return Arrays.deepToString(this.data);
    }
}
