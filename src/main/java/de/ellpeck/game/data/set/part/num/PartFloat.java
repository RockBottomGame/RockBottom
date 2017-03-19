package de.ellpeck.game.data.set.part.num;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PartFloat extends BasicDataPart<Float>{

    public PartFloat(String name){
        super(name);
    }

    @Override
    public void write(DataOutputStream stream) throws Exception{
        stream.writeFloat(this.data);
    }

    @Override
    public void read(DataInputStream stream) throws Exception{
        this.data = stream.readFloat();
    }

    public PartFloat(String name, Float data){
        super(name, data);
    }
}