package de.ellpeck.rockbottom.assets.tex;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Texture implements ITexture{

    private static Texture boundTexture;

    private Random rand;
    private Map<String, JsonElement> additionalData;
    private List<ITexture> variations;

    private final int id;
    private int width;
    private int height;
    private ByteBuffer pixelData;

    public Texture(int width, int height, ByteBuffer data){
        this(GL11.GL_NEAREST);
        this.width = width;
        this.height = height;
        this.pixelData = data;

        this.init();
    }

    public Texture(InputStream stream) throws IOException{
        this(GL11.GL_NEAREST);
        this.load(stream);
    }

    public Texture(int filter){
        this.id = GL11.glGenTextures();

        this.param(GL11.GL_TEXTURE_MIN_FILTER, filter);
        this.param(GL11.GL_TEXTURE_MAG_FILTER, filter);
    }

    @Override
    public Texture getSubTexture(float x, float y, float width, float height){
        return this.getSubTexture(x, y, width, height, true, true);
    }

    @Override
    public Texture getSubTexture(float x, float y, float width, float height, boolean inheritVariations, boolean inheritData){
        //TODO Sub textures

        /*if(inheritData){
            sub.setAdditionalData(this.additionalData);
        }
        if(inheritVariations){
            if(this.variations != null){
                List<ITexture> newVariations = new ArrayList<>();
                for(ITexture variation : this.variations){
                    newVariations.add(variation.getSubTexture(x, y, width, height, inheritVariations, inheritData));
                }
                sub.setVariations(newVariations);
            }
        }*/

        return this;
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
        IRenderer renderer = RockBottomAPI.getGame().getRenderer();
        renderer.setTexture(this);

        //TODO Add light-based coloring back

        float u = srcX/this.width;
        float v = srcY/this.height;
        float u2 = srcX2/this.width;
        float v2 = srcY2/this.height;

        renderer.addVertex(x, y, filter, u, v);
        renderer.addVertex(x, y2, filter, u, v2);
        renderer.addVertex(x2, y2, filter, u2, v2);

        renderer.addVertex(x, y, filter, u, v);
        renderer.addVertex(x2, y2, filter, u2, v2);
        renderer.addVertex(x2, y, filter, u2, v);
    }

    private int combineLight(int[] light, int corner, int filter){
        if(light != null){
            return Colors.multiply(light[corner], filter);
        }
        else{
            return filter;
        }
    }

    @Override
    public int getTextureColor(int x, int y){
        //TODO Get texture colors
        return 0;
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

    private void load(InputStream stream) throws IOException{
        byte[] input = ByteStreams.toByteArray(stream);
        ByteBuffer data = BufferUtils.createByteBuffer(input.length);
        data.put(input);
        data.flip();

        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer width = stack.mallocInt(1);
        IntBuffer height = stack.mallocInt(1);

        this.pixelData = STBImage.stbi_load_from_memory(data, width, height, stack.mallocInt(1), 4);
        if(this.pixelData == null){
            throw new IOException("Failed to load texture :\n"+STBImage.stbi_failure_reason());
        }

        this.width = width.get();
        this.height = height.get();

        stack.pop();

        this.init();
    }

    @Override
    public void bind(){
        if(boundTexture != this){
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
            boundTexture = this;
        }
    }

    private void init(){
        this.bind();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.pixelData);
    }

    @Override
    public void param(int param, int value){
        this.bind();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, param, value);
    }

    @Override
    public int getId(){
        return this.id;
    }

    @Override
    public int getWidth(){
        return this.width;
    }

    @Override
    public int getHeight(){
        return this.height;
    }

    @Override
    public ByteBuffer getPixelData(){
        return this.pixelData;
    }

    @Override
    public void unbind(){
        if(boundTexture == this){
            unbindAll();
        }
    }

    public static void unbindAll(){
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        boundTexture = null;
    }

    @Override
    public void dispose(){
        this.unbind();
        GL11.glDeleteTextures(this.id);
    }
}
