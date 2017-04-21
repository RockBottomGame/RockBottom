package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.data.set.DataSet;
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

    public static void writeStringToBuffer(String s, ByteBuf buf){
        buf.writeInt(s.length());

        for(char c : s.toCharArray()){
            buf.writeChar(c);
        }
    }

    public static String readStringFromBuffer(ByteBuf buf){
        char[] chars = new char[buf.readInt()];

        for(int i = 0; i < chars.length; i++){
            chars[i] = buf.readChar();
        }

        return new String(chars);
    }
}
