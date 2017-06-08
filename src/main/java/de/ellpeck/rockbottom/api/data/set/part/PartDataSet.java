package de.ellpeck.rockbottom.api.data.set.part;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;

import java.io.DataInput;
import java.io.DataOutput;

public class PartDataSet extends BasicDataPart<DataSet>{

    public PartDataSet(String name){
        super(name);
    }

    public PartDataSet(String name, DataSet data){
        super(name, data);
    }

    @Override
    public void write(DataOutput stream) throws Exception{
        RockBottomAPI.getApiHandler().writeSet(stream, this.data);
    }

    @Override
    public void read(DataInput stream) throws Exception{
        this.data = new DataSet();
        RockBottomAPI.getApiHandler().readSet(stream, this.data);
    }
}
