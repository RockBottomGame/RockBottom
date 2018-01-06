package de.ellpeck.rockbottom.assets.stub;

import com.google.gson.JsonElement;
import com.sun.prism.impl.BufferUtil;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.render.engine.TextureBank;
import de.ellpeck.rockbottom.api.util.Colors;

import java.nio.ByteBuffer;
import java.util.Random;

public class EmptyTexture implements ITexture{

    private final ByteBuffer pixelData = BufferUtil.newByteBuffer(0);

    @Override
    public void bind(TextureBank bank){

    }

    @Override
    public void bind(){

    }

    @Override
    public void param(int param, int value){

    }

    @Override
    public int getId(){
        return 0;
    }

    @Override
    public int getTextureWidth(){
        return 0;
    }

    @Override
    public int getTextureHeight(){
        return 0;
    }

    @Override
    public ByteBuffer getPixelData(){
        return this.pixelData;
    }

    @Override
    public void unbind(TextureBank bank){

    }

    @Override
    public void unbind(){

    }

    @Override
    public void draw(float x, float y){

    }

    @Override
    public void draw(float x, float y, float scale){

    }

    @Override
    public void draw(float x, float y, float width, float height){

    }

    @Override
    public void draw(float x, float y, float width, float height, int[] light){

    }

    @Override
    public void draw(float x, float y, float width, float height, int filter){

    }

    @Override
    public void draw(float x, float y, float width, float height, int[] light, int filter){

    }

    @Override
    public void draw(float x, float y, float x2, float y2, float srcX, float srcY, float srcX2, float srcY2){

    }

    @Override
    public void draw(float x, float y, float x2, float y2, float srcX, float srcY, float srcX2, float srcY2, int[] light){

    }

    @Override
    public void draw(float x, float y, float x2, float y2, float srcX, float srcY, float srcX2, float srcY2, int filter){

    }

    @Override
    public void draw(float x, float y, float x2, float y2, float srcX, float srcY, float srcX2, float srcY2, int[] light, int filter){

    }

    @Override
    public void draw(float x, float y, float x2, float y2, float x3, float y3, float x4, float y4, float srcX, float srcY, float srcX2, float srcY2, int[] light, int filter){

    }

    @Override
    public JsonElement getAdditionalData(String name){
        return null;
    }

    @Override
    public ITexture getVariation(Random random){
        return this;
    }

    @Override
    public ITexture getPositionalVariation(int x, int y){
        return this;
    }

    @Override
    public ITexture getSubTexture(int x, int y, int width, int height){
        return this;
    }

    @Override
    public ITexture getSubTexture(int x, int y, int width, int height, boolean inheritVariations, boolean inheritData){
        return this;
    }

    @Override
    public int getTextureColor(int x, int y){
        return Colors.TRANSPARENT;
    }

    @Override
    public int getRenderWidth(){
        return 0;
    }

    @Override
    public int getRenderHeight(){
        return 0;
    }

    @Override
    public int getRenderOffsetX(){
        return 0;
    }

    @Override
    public int getRenderOffsetY(){
        return 0;
    }

    @Override
    public void dispose(){

    }
}
