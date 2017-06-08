package de.ellpeck.rockbottom.game.data.set;

import de.ellpeck.rockbottom.game.data.DataManager;
import de.ellpeck.rockbottom.game.data.set.part.*;
import de.ellpeck.rockbottom.game.data.set.part.num.*;
import de.ellpeck.rockbottom.game.data.set.part.num.array.PartByteByteArray;
import de.ellpeck.rockbottom.game.data.set.part.num.array.PartIntArray;
import de.ellpeck.rockbottom.game.data.set.part.num.array.PartShortShortArray;
import org.newdawn.slick.util.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataSet{

    private final Map<String, DataPart> data = new HashMap<>();

    public static void writeSet(DataOutput stream, DataSet set) throws Exception{
        stream.writeInt(set.data.size());

        for(DataPart part : set.data.values()){
            writePart(stream, part);
        }
    }

    public static void readSet(DataInput stream, DataSet set) throws Exception{
        int amount = stream.readInt();

        for(int i = 0; i < amount; i++){
            DataPart part = readPart(stream);
            set.data.put(part.getName(), part);
        }
    }

    public static void writePart(DataOutput stream, DataPart part) throws Exception{
        stream.writeByte(DataManager.PART_REGISTRY.getId(part.getClass()));
        stream.writeUTF(part.getName());
        part.write(stream);
    }

    public static DataPart readPart(DataInput stream) throws Exception{
        int id = stream.readByte();
        String name = stream.readUTF();

        Class<? extends DataPart> partClass = DataManager.PART_REGISTRY.get(id);
        DataPart part = partClass.getConstructor(String.class).newInstance(name);
        part.read(stream);

        return part;
    }

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
        try{
            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
            writeSet(stream, this);
            stream.close();
        }
        catch(Exception e){
            Log.error("Exception saving a data set to disk!", e);
        }
    }

    public void read(File file){
        if(!this.data.isEmpty()){
            this.data.clear();
        }

        try{
            if(file.exists()){
                DataInputStream stream = new DataInputStream(new FileInputStream(file));
                readSet(stream, this);
                stream.close();
            }
        }
        catch(Exception e){
            Log.error("Exception loading a data set from disk!", e);
        }
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
