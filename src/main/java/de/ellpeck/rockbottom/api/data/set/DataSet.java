package de.ellpeck.rockbottom.api.data.set;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.part.*;
import de.ellpeck.rockbottom.api.data.set.part.num.*;
import de.ellpeck.rockbottom.api.data.set.part.num.array.PartByteByteArray;
import de.ellpeck.rockbottom.api.data.set.part.num.array.PartIntArray;
import de.ellpeck.rockbottom.api.data.set.part.num.array.PartShortShortArray;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataSet{

    public final Map<String, DataPart> data = new HashMap<>();

    public void addPart(DataPart part){
        this.data.put(part.getName(), part);
    }

    public <T> T getPartContent(String key, Class<? extends DataPart<T>> typeClass, T defaultValue){
        DataPart part = this.data.get(key);

        if(part != null && part.getClass() == typeClass){
            T result = (T)part.get();
            if(result != null){
                return result;
            }
        }

        return defaultValue;
    }

    public int getInt(String key){
        return this.getPartContent(key, PartInt.class, 0);
    }

    public void addInt(String key, int i){
        this.addPart(new PartInt(key, i));
    }

    public long getLong(String key){
        return this.getPartContent(key, PartLong.class, 0L);
    }

    public void addLong(String key, long l){
        this.addPart(new PartLong(key, l));
    }

    public float getFloat(String key){
        return this.getPartContent(key, PartFloat.class, 0F);
    }

    public void addFloat(String key, float f){
        this.addPart(new PartFloat(key, f));
    }

    public double getDouble(String key){
        return this.getPartContent(key, PartDouble.class, 0D);
    }

    public void addDouble(String key, double d){
        this.addPart(new PartDouble(key, d));
    }

    public DataSet getDataSet(String key){
        return this.getPartContent(key, PartDataSet.class, new DataSet());
    }

    public void addDataSet(String key, DataSet set){
        this.addPart(new PartDataSet(key, set));
    }

    public byte[][] getByteByteArray(String key, int defaultSize){
        return this.getPartContent(key, PartByteByteArray.class, new byte[defaultSize][defaultSize]);
    }

    public void addByteByteArray(String key, byte[][] array){
        this.addPart(new PartByteByteArray(key, array));
    }

    public int[] getIntArray(String key, int defaultSize){
        return this.getPartContent(key, PartIntArray.class, new int[defaultSize]);
    }

    public void addIntArray(String key, int[] array){
        this.addPart(new PartIntArray(key, array));
    }

    public short[][] getShortShortArray(String key, int defaultSize){
        return this.getPartContent(key, PartShortShortArray.class, new short[defaultSize][defaultSize]);
    }

    public void addShortShortArray(String key, short[][] array){
        this.addPart(new PartShortShortArray(key, array));
    }

    public UUID getUniqueId(String key){
        return this.getPartContent(key, PartUniqueId.class, null);
    }

    public void addUniqueId(String key, UUID id){
        this.addPart(new PartUniqueId(key, id));
    }

    public byte getByte(String key){
        return this.getPartContent(key, PartByte.class, (byte)0);
    }

    public void addByte(String key, byte b){
        this.addPart(new PartByte(key, b));
    }

    public short getShort(String key){
        return this.getPartContent(key, PartShort.class, (short)0);
    }

    public void addShort(String key, short s){
        this.addPart(new PartShort(key, s));
    }

    public boolean getBoolean(String key){
        return this.getPartContent(key, PartBoolean.class, false);
    }

    public void addBoolean(String key, boolean s){
        this.addPart(new PartBoolean(key, s));
    }

    public String getString(String key){
        return this.getPartContent(key, PartString.class, null);
    }

    public void addString(String key, String s){
        this.addPart(new PartString(key, s));
    }

    public void write(File file){
        RockBottomAPI.getApiHandler().writeDataSet(this, file);
    }

    public void read(File file){
        RockBottomAPI.getApiHandler().readDataSet(this, file);
    }

    @Override
    public String toString(){
        return this.data.toString();
    }

    public Map<String, DataPart> getData(){
        return this.data;
    }

    public boolean isEmpty(){
        return this.data.isEmpty();
    }
}
