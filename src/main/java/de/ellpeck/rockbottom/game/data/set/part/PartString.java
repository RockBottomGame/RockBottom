package de.ellpeck.rockbottom.game.data.set.part;

import java.io.DataInput;
import java.io.DataOutput;

public class PartString extends BasicDataPart<String>{

    public PartString(String name, String data){
        super(name, data);
    }

    public PartString(String name){
        super(name);
    }

    @Override
    public void write(DataOutput stream) throws Exception{
        stream.writeInt(this.data.length());

        for(char c : this.data.toCharArray()){
            stream.writeChar(c);
        }
    }

    @Override
    public void read(DataInput stream) throws Exception{
        char[] chars = new char[stream.readInt()];

        for(int i = 0; i < chars.length; i++){
            chars[i] = stream.readChar();
        }

        this.data = new String(chars);
    }
}
