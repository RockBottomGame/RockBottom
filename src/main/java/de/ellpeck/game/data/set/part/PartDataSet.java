package de.ellpeck.game.data.set.part;

import de.ellpeck.game.data.set.DataSet;

import java.io.*;

public class PartDataSet extends BasicDataPart<DataSet>{

    public PartDataSet(String name){
        super(name);
    }

    @Override
    public void write(DataOutput stream) throws Exception{
        DataSet.writeSet(stream, this.data);
    }

    @Override
    public void read(DataInput stream) throws Exception{
        this.data = new DataSet();
        DataSet.readSet(stream, this.data);
    }

    public PartDataSet(String name, DataSet data){
        super(name, data);
    }
}
