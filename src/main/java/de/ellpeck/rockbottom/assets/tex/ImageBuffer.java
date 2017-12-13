package de.ellpeck.rockbottom.assets.tex;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageBuffer implements ImageData{

    private final int width;
    private final int height;
    private final int texWidth;
    private final int texHeight;
    private final byte[] rawData;

    public ImageBuffer(int width, int height){
        this.width = width;
        this.height = height;

        this.texWidth = InternalTextureLoader.get2Fold(width);
        this.texHeight = InternalTextureLoader.get2Fold(height);

        this.rawData = new byte[this.texWidth*this.texHeight*4];
    }

    public byte[] getRGBA(){
        return this.rawData;
    }

    @Override
    public int getDepth(){
        return 32;
    }

    @Override
    public int getHeight(){
        return this.height;
    }

    @Override
    public int getTexHeight(){
        return this.texHeight;
    }

    @Override
    public int getTexWidth(){
        return this.texWidth;
    }

    @Override
    public int getWidth(){
        return this.width;
    }

    @Override
    public ByteBuffer getImageBufferData(){
        ByteBuffer scratch = BufferUtils.createByteBuffer(this.rawData.length);
        scratch.put(this.rawData);
        scratch.flip();

        return scratch;
    }

    public void setRGBA(int x, int y, int r, int g, int b, int a){
        int offset = ((x+(y*this.texWidth))*4);

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

