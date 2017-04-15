package de.ellpeck.game.data.set.part;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.UUID;

public class PartUniqueId extends BasicDataPart<UUID>{

    public PartUniqueId(String name, UUID data){
        super(name, data);
    }

    public PartUniqueId(String name){
        super(name);
    }

    @Override
    public void write(DataOutput stream) throws Exception{
        stream.writeLong(this.data.getMostSignificantBits());
        stream.writeLong(this.data.getLeastSignificantBits());
    }

    @Override
    public void read(DataInput stream) throws Exception{
        this.data = new UUID(stream.readLong(), stream.readLong());
    }
}
