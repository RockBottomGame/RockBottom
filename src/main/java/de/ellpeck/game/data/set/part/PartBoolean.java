package de.ellpeck.game.data.set.part;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PartBoolean extends BasicDataPart<Boolean>{

    public PartBoolean(String name, Boolean data){
        super(name, data);
    }

    public PartBoolean(String name){
        super(name);
    }

    @Override
    public void write(DataOutputStream stream) throws Exception{
        stream.writeBoolean(this.data);
    }

    @Override
    public void read(DataInputStream stream) throws Exception{
        this.data = stream.readBoolean();
    }
}
