package de.ellpeck.game.data.set.part.num;

import de.ellpeck.game.data.set.part.BasicDataPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PartDouble extends BasicDataPart<Double>{

    public PartDouble(String name){
        super(name);
    }

    @Override
    public void write(DataOutputStream stream) throws Exception{
        stream.writeDouble(this.data);
    }

    @Override
    public void read(DataInputStream stream) throws Exception{
        this.data = stream.readDouble();
    }

    public PartDouble(String name, Double data){
        super(name, data);
    }
}
