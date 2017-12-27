package de.ellpeck.rockbottom.assets.tex;

import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RenderedTexture implements ITexture{

    private Random rand;
    private Map<String, JsonElement> additionalData;
    private List<ITexture> variations;

    private final Texture underlyingTexture;
    private final byte[] pixelData;
    private final int underlyingWidth;
    private final int underlyingHeight;
    private final boolean hasAlpha;
    private float width;
    private float height;
    private float textureWidth;
    private float textureHeight;
    private float textureOffsetX;
    private float textureOffsetY;
    private float centerX;
    private float centerY;
    private float angle;

    public RenderedTexture(InputStream stream, boolean flipped) throws IOException{
        this(TextureLoader.getTexture("PNG", stream, flipped, GL11.GL_NEAREST));
    }

    public RenderedTexture(Texture tex){
        this.underlyingTexture = tex;
        this.pixelData = tex.getTextureData();
        this.width = tex.getImageWidth();
        this.height = tex.getImageHeight();
        this.underlyingWidth = tex.getTextureWidth();
        this.underlyingHeight = tex.getTextureHeight();
        this.textureWidth = this.underlyingWidth;
        this.textureHeight = this.underlyingHeight;
        this.hasAlpha = tex.hasAlpha();
        this.centerX = this.width/2F;
        this.centerY = this.height/2F;
    }

    @Override
    public RenderedTexture getSubTexture(float x, float y, float width, float height){
        return this.getSubTexture(x, y, width, height, true, true);
    }

    @Override
    public RenderedTexture getSubTexture(float x, float y, float width, float height, boolean inheritVariations, boolean inheritData){
        float texOffsetX = ((x/this.width)*this.textureWidth)+this.textureOffsetX;
        float texOffsetY = ((y/this.height)*this.textureHeight)+this.textureOffsetY;
        float texWidth = ((width/this.width)*this.textureWidth);
        float texHeight = ((height/this.height)*this.textureHeight);

        RenderedTexture sub = new RenderedTexture(this.underlyingTexture);

        sub.textureOffsetX = texOffsetX;
        sub.textureOffsetY = texOffsetY;
        sub.textureWidth = texWidth;
        sub.textureHeight = texHeight;

        sub.width = width;
        sub.height = height;
        sub.centerX = width/2F;
        sub.centerY = height/2F;

        if(inheritData){
            sub.setAdditionalData(this.additionalData);
        }
        if(inheritVariations){
            sub.setVariations(this.variations);
        }

        return sub;
    }

    public void setAdditionalData(Map<String, JsonElement> data){
        this.additionalData = data;
    }

    public void setVariations(List<ITexture> variations){
        this.variations = variations;
    }

    @Override
    public JsonElement getAdditionalData(String name){
        return this.additionalData != null ? this.additionalData.get(name) : null;
    }

    @Override
    public void draw(float x, float y){
        this.draw(x, y, 1F);
    }

    @Override
    public void draw(float x, float y, float scale){
        this.draw(x, y, this.width*1F, this.height*1F);
    }

    @Override
    public void draw(float x, float y, float width, float height){
        this.draw(x, y, width, height, Colors.WHITE);
    }

    @Override
    public void draw(float x, float y, float width, float height, int[] light){
        this.draw(x, y, width, height, light, Colors.WHITE);
    }

    @Override
    public void draw(float x, float y, float width, float height, int filter){
        this.draw(x, y, x+width, y+height, 0, 0, this.width, this.height, null, filter);
    }

    @Override
    public void draw(float x, float y, float width, float height, int[] light, int filter){
        this.draw(x, y, x+width, y+height, 0, 0, this.width, this.height, light, filter);
    }

    @Override
    public void draw(float x, float y, float x2, float y2, float srcX, float srcY, float srcX2, float srcY2){
        this.draw(x, y, x2, y2, srcX, srcY, srcX2, srcY2, Colors.WHITE);
    }

    @Override
    public void draw(float x, float y, float x2, float y2, float srcX, float srcY, float srcX2, float srcY2, int[] light){
        this.draw(x, y, x2, y2, srcX, srcY, srcX2, srcY2, light, Colors.WHITE);
    }

    @Override
    public void draw(float x, float y, float x2, float y2, float srcX, float srcY, float srcX2, float srcY2, int filter){
        this.draw(x, y, x2, y2, srcX, srcY, srcX2, srcY2, null, filter);
    }

    @Override
    public void draw(float x, float y, float x2, float y2, float srcX, float srcY, float srcX2, float srcY2, int[] light, int filter){
        this.bind();

        GL11.glTranslatef(x, y, 0F);
        if(this.angle != 0F){
            GL11.glTranslatef(this.centerX, this.centerY, 0F);
            GL11.glRotatef(this.angle, 0F, 0F, 1F);
            GL11.glTranslatef(-this.centerX, -this.centerY, 0F);
        }

        GL11.glBegin(GL11.GL_QUADS);
        this.drawEmbedded(x, y, x2, y2, srcX, srcY, srcX2, srcY2, light, filter);
        GL11.glEnd();

        if(this.angle != 0F){
            GL11.glTranslatef(this.centerX, this.centerY, 0F);
            GL11.glRotatef(-this.angle, 0F, 0F, 1F);
            GL11.glTranslatef(-this.centerX, -this.centerY, 0F);
        }
        GL11.glTranslatef(-x, -y, 0F);
    }

    @Override
    public float getWidth(){
        return this.width;
    }

    @Override
    public float getHeight(){
        return this.height;
    }

    @Override
    public int getUnderlyingWidth(){
        return this.underlyingWidth;
    }

    @Override
    public int getUnderlyingHeight(){
        return this.underlyingHeight;
    }

    @Override
    public float getTextureWidth(){
        return this.textureWidth;
    }

    @Override
    public float getTextureHeight(){
        return this.textureHeight;
    }

    @Override
    public float getTextureOffsetX(){
        return this.textureOffsetX;
    }

    @Override
    public float getTextureOffsetY(){
        return this.textureOffsetY;
    }

    @Override
    public float getCenterX(){
        return this.centerX;
    }

    @Override
    public float getCenterY(){
        return this.centerY;
    }

    @Override
    public float getAngle(){
        return this.angle;
    }

    private void drawEmbedded(float x, float y, float x2, float y2, float srcX, float srcY, float srcX2, float srcY2, int[] light, int filter){
        float width = x2-x;
        float height = y2-y;

        float texOffX = srcX/this.underlyingWidth+this.textureOffsetX;
        float texOffY = srcY/this.underlyingHeight+this.textureOffsetY;
        float texWidth = (srcX2-srcX)/this.underlyingWidth;
        float texHeight = (srcY2-srcY)/this.underlyingHeight;

        this.bindLight(light, TOP_LEFT, filter);
        GL11.glTexCoord2f(texOffX, texOffY);
        GL11.glVertex3f(0F, 0F, 0F);
        this.bindLight(light, BOTTOM_LEFT, filter);
        GL11.glTexCoord2f(texOffX, texOffY+texHeight);
        GL11.glVertex3f(0F, height, 0F);
        this.bindLight(light, BOTTOM_RIGHT, filter);
        GL11.glTexCoord2f(texOffX+texWidth, texOffY+texHeight);
        GL11.glVertex3f(width, height, 0F);
        this.bindLight(light, TOP_RIGHT, filter);
        GL11.glTexCoord2f(texOffX+texWidth, texOffY);
        GL11.glVertex3f(width, 0F, 0F);
    }

    private void bindLight(int[] light, int index, int filter){
        if(light != null){
            Colors.bind(Colors.multiply(light[index], filter));
        }
        else{
            Colors.bind(filter);
        }
    }

    @Override
    public int getTextureColor(int x, int y){
        int offX = (int)(this.textureOffsetX*this.underlyingWidth);
        int offY = (int)(this.textureOffsetY*this.underlyingHeight);

        x = (this.textureWidth < 0 ? offX-x : offX+x)%this.underlyingWidth;
        y = (this.textureHeight < 0 ? offY-y : offY+y)%this.underlyingHeight;

        int offset = (x+(y*this.underlyingHeight))*(this.hasAlpha() ? 4 : 3);

        if(this.hasAlpha()){
            return Colors.rgb(this.translate(this.pixelData[offset]), this.translate(this.pixelData[offset+1]), this.translate(this.pixelData[offset+2]), this.translate(this.pixelData[offset+3]));
        }
        else{
            return Colors.rgb(this.translate(this.pixelData[offset]), this.translate(this.pixelData[offset+1]), this.translate(this.pixelData[offset+2]));
        }
    }

    @Override
    public void setRotation(float angle){
        this.angle = angle;
    }

    @Override
    public void setRotationCenter(float x, float y){
        this.centerX = x;
        this.centerY = y;
    }

    private int translate(byte b){
        return b < 0 ? 256+b : b;
    }

    @Override
    public RenderedTexture copyAndFlip(boolean flipHorizontal, boolean flipVertical){
        RenderedTexture copy = this.getCopy();

        if(flipHorizontal){
            copy.textureOffsetX = this.textureOffsetX+this.textureWidth;
            copy.textureWidth = -this.textureWidth;
        }
        if(flipVertical){
            copy.textureOffsetY = this.textureOffsetY+this.textureHeight;
            copy.textureHeight = -this.textureHeight;
        }

        return copy;
    }

    @Override
    public RenderedTexture getCopy(){
        return this.getSubTexture(0F, 0F, this.width, this.height);
    }

    @Override
    public ITexture getVariation(Random random){
        if(this.variations == null){
            return this;
        }
        else{
            int index = random.nextInt(this.variations.size()+1);

            if(index == 0){
                return this;
            }
            else{
                return this.variations.get(index-1);
            }
        }
    }

    @Override
    public ITexture getPositionalVariation(int x, int y){
        if(this.variations == null){
            return this;
        }
        else{
            if(this.rand == null){
                this.rand = new Random();
            }

            this.rand.setSeed(Util.scrambleSeed(x, y));
            return this.getVariation(this.rand);
        }
    }

    @Override
    public void bind(){
        this.underlyingTexture.bind();
    }

    @Override
    public boolean hasAlpha(){
        return this.hasAlpha;
    }
}
