package de.ellpeck.rockbottom.api;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.set.part.DataPart;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;

public interface IApiHandler{

    void writeDataSet(DataSet set, File file);

    void readDataSet(DataSet set, File file);

    void writeSet(DataOutput stream, DataSet set) throws Exception;

    void readSet(DataInput stream, DataSet set) throws Exception;

    void writePart(DataOutput stream, DataPart part) throws Exception;

    DataPart readPart(DataInput stream) throws Exception;
}
