package de.ellpeck.game.data.set.part.num;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PartShort extends BasicDataPart<Short>{

    public PartShort(String name){
        super(name);
    }

    @Override
    public void write(DataOutputStream stream) throws IOException{
        stream.writeShort(this.data);
    }

    @Override
    public void read(DataInputStream stream) throws IOException{
        this.data = stream.readShort();
    }

    public PartShort(String name, Short data){
        super(name, data);
    }
}
