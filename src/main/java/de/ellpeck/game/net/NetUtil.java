package de.ellpeck.game.net;

import de.ellpeck.game.data.set.DataSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.newdawn.slick.util.Log;

public final class NetUtil{

    public static void writeSetToBuffer(DataSet set, ByteBuf buf){
        try{
            DataSet.writeSet(new ByteBufOutputStream(buf), set);
        }
        catch(Exception e){
            Log.error("Couldn't write data set to buffer", e);
        }
    }

    public static void readSetFromBuffer(DataSet set, ByteBuf buf){
        try{
            DataSet.readSet(new ByteBufInputStream(buf), set);
        }
        catch(Exception e){
            Log.error("Couldn't read data set from buffer", e);
        }
    }

}
