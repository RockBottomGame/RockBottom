package de.ellpeck.rockbottom.api.data.set.part.num;

import de.ellpeck.rockbottom.api.data.set.part.BasicDataPart;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PartByte extends BasicDataPart<Byte>{

    public PartByte(String name){
        super(name);
    }

    public PartByte(String name, Byte data){
        super(name, data);
    }

    @Override
    public void write(DataOutput stream) throws IOException{
        stream.writeByte(this.data);
    }

    @Override
    public void read(DataInput stream) throws IOException{
        this.data = stream.readByte();
    }
}
