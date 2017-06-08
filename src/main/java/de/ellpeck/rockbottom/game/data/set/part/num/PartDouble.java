package de.ellpeck.rockbottom.game.data.set.part.num;

import de.ellpeck.rockbottom.game.data.set.part.BasicDataPart;

import java.io.DataInput;
import java.io.DataOutput;

public class PartDouble extends BasicDataPart<Double>{

    public PartDouble(String name){
        super(name);
    }

    public PartDouble(String name, Double data){
        super(name, data);
    }

    @Override
    public void write(DataOutput stream) throws Exception{
        stream.writeDouble(this.data);
    }

    @Override
    public void read(DataInput stream) throws Exception{
        this.data = stream.readDouble();
    }
}
