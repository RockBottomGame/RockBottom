package de.ellpeck.rockbottom.data.set.part.num;

import de.ellpeck.rockbottom.data.set.part.BasicDataPart;

import java.io.*;

public class PartShort extends BasicDataPart<Short>{

    public PartShort(String name){
        super(name);
    }

    @Override
    public void write(DataOutput stream) throws IOException{
        stream.writeShort(this.data);
    }

    @Override
    public void read(DataInput stream) throws IOException{
        this.data = stream.readShort();
    }

    public PartShort(String name, Short data){
        super(name, data);
    }
}
