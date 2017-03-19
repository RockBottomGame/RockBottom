package de.ellpeck.game.data.set.part.num;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PartLong extends BasicDataPart<Long>{

    public PartLong(String name){
        super(name);
    }

    @Override
    public void write(DataOutputStream stream) throws Exception{
        stream.writeLong(this.data);
    }

    @Override
    public void read(DataInputStream stream) throws Exception{
        this.data = stream.readLong();
    }

    public PartLong(String name, Long data){
        super(name, data);
    }
}
