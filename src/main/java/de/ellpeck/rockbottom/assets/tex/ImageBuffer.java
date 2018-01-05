package de.ellpeck.rockbottom.assets.tex;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageBuffer{

    private final int width;
    private final byte[] rawData;

    public ImageBuffer(int width, int height){
        this.width = width;
        this.rawData = new byte[width*height*4];
    }

    public ByteBuffer getRGBA(){
        ByteBuffer buffer = BufferUtils.createByteBuffer(this.rawData.length);
        buffer.put(this.rawData);
        buffer.flip();
        return buffer;
    }

    public void setRGBA(int x, int y, int r, int g, int b, int a){
        int offset = ((x+(y*this.width))*4);

        if(ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN){
            this.rawData[offset] = (byte)b;
            this.rawData[offset+1] = (byte)g;
            this.rawData[offset+2] = (byte)r;
            this.rawData[offset+3] = (byte)a;
        }
        else{
            this.rawData[offset] = (byte)r;
            this.rawData[offset+1] = (byte)g;
            this.rawData[offset+2] = (byte)b;
            this.rawData[offset+3] = (byte)a;
        }
    }
}

