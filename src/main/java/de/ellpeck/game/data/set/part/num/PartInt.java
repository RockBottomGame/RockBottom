package de.ellpeck.game.data.set.part.num;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PartInt extends BasicDataPart<Integer>{

    public PartInt(String name){
        super(name);
    }

    @Override
    public void write(DataOutputStream stream) throws IOException{
        stream.writeInt(this.data);
    }

    @Override
    public void read(DataInputStream stream) throws IOException{
        this.data = stream.readInt();
    }

    public PartInt(String name, Integer data){
        super(name, data);
    }
}
