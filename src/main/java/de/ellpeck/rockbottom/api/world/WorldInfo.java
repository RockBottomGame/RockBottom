package de.ellpeck.rockbottom.api.world;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import io.netty.buffer.ByteBuf;

import java.io.File;

public class WorldInfo{

    private final File dataFile;

    public long seed;
    public int totalTimeInWorld;
    public int currentWorldTime;

    public WorldInfo(File worldDirectory){
        this.dataFile = new File(worldDirectory, "world_info.dat");
    }

    public void load(){
        DataSet dataSet = new DataSet();
        dataSet.read(this.dataFile);

        this.seed = dataSet.getLong("seed");
        this.totalTimeInWorld = dataSet.getInt("total_time");
        this.currentWorldTime = dataSet.getInt("curr_time");
    }

    public void save(){
        DataSet dataSet = new DataSet();
        dataSet.addLong("seed", this.seed);
        dataSet.addInt("total_time", this.totalTimeInWorld);
        dataSet.addInt("curr_time", this.currentWorldTime);
        dataSet.write(this.dataFile);
    }

    public void toBuffer(ByteBuf buf){
        buf.writeLong(this.seed);
        buf.writeInt(this.totalTimeInWorld);
        buf.writeInt(this.currentWorldTime);
    }

    public void fromBuffer(ByteBuf buf){
        this.seed = buf.readLong();
        this.totalTimeInWorld = buf.readInt();
        this.currentWorldTime = buf.readInt();
    }
}
