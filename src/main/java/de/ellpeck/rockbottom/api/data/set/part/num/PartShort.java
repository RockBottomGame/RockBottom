package de.ellpeck.rockbottom.api.data.set.part.num;

import de.ellpeck.rockbottom.api.data.set.part.BasicDataPart;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PartShort extends BasicDataPart<Short>{

    public PartShort(String name){
        super(name);
    }

    public PartShort(String name, Short data){
        super(name, data);
    }

    @Override
    public void write(DataOutput stream) throws IOException{
        stream.writeShort(this.data);
    }

    @Override
    public void read(DataInput stream) throws IOException{
        this.data = stream.readShort();
    }
}
