package de.ellpeck.game.data.set.part.num;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.io.*;

public class PartByte extends BasicDataPart<Byte>{

    public PartByte(String name){
        super(name);
    }

    @Override
    public void write(DataOutput stream) throws IOException{
        stream.writeByte(this.data);
    }

    @Override
    public void read(DataInput stream) throws IOException{
        this.data = stream.readByte();
    }

    public PartByte(String name, Byte data){
        super(name, data);
    }
}
