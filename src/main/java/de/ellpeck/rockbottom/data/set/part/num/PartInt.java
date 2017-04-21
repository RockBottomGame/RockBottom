package de.ellpeck.rockbottom.data.set.part.num;

import de.ellpeck.rockbottom.data.set.part.BasicDataPart;

import java.io.*;

public class PartInt extends BasicDataPart<Integer>{

    public PartInt(String name){
        super(name);
    }

    @Override
    public void write(DataOutput stream) throws IOException{
        stream.writeInt(this.data);
    }

    @Override
    public void read(DataInput stream) throws IOException{
        this.data = stream.readInt();
    }

    public PartInt(String name, Integer data){
        super(name, data);
    }
}
