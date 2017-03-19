package de.ellpeck.game.data.set.part;

import de.ellpeck.game.data.set.DataSet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PartDataSet extends BasicDataPart<DataSet>{

    public PartDataSet(String name){
        super(name);
    }

    @Override
    public void write(DataOutputStream stream) throws Exception{
        DataSet.writeSet(stream, this.data);
    }

    @Override
    public void read(DataInputStream stream) throws Exception{
        this.data = new DataSet();
        DataSet.readSet(stream, this.data);
    }

    public PartDataSet(String name, DataSet data){
        super(name, data);
    }
}
