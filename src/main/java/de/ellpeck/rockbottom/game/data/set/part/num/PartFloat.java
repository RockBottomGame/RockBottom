package de.ellpeck.rockbottom.game.data.set.part.num;

import de.ellpeck.rockbottom.game.data.set.part.BasicDataPart;

import java.io.DataInput;
import java.io.DataOutput;

public class PartFloat extends BasicDataPart<Float>{

    public PartFloat(String name){
        super(name);
    }

    public PartFloat(String name, Float data){
        super(name, data);
    }

    @Override
    public void write(DataOutput stream) throws Exception{
        stream.writeFloat(this.data);
    }

    @Override
    public void read(DataInput stream) throws Exception{
        this.data = stream.readFloat();
    }
}
