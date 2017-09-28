package de.ellpeck.rockbottom.assets;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ellpeck.rockbottom.api.assets.anim.IAnimation;
import de.ellpeck.rockbottom.api.assets.tex.ITexture;
import de.ellpeck.rockbottom.api.util.Util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Animation implements IAnimation{

    private final int frameWidth;
    private final int frameHeight;

    private final ITexture texture;
    private final List<AnimationRow> rows;

    public Animation(ITexture texture, int frameWidth, int frameHeight, List<AnimationRow> rows){
        this.texture = texture;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.rows = rows;
    }

    public static Animation fromStream(ITexture texture, InputStream infoStream) throws Exception{
        List<AnimationRow> rows = new ArrayList<>();

        JsonObject main = new JsonParser().parse(new InputStreamReader(infoStream, Charsets.UTF_8)).getAsJsonObject();
        JsonArray dims = main.getAsJsonArray("size");
        int frameWidth = dims.get(0).getAsInt();
        int frameHeight = dims.get(1).getAsInt();

        JsonArray data = main.getAsJsonArray("data");
        for(JsonElement element : data){
            JsonArray array = element.getAsJsonArray();
            float[] times = new float[array.size()];

            for(int i = 0; i < times.length; i++){
                times[i] = array.get(i).getAsFloat();
            }

            rows.add(new AnimationRow(times));
        }

        return new Animation(texture, frameWidth, frameHeight, rows);
    }

    @Override
    public void drawFrame(int row, int frame, float x, float y, float width, float height, int filter){
        this.drawFrame(row, frame, x, y, width, height, null, filter);
    }

    @Override
    public void drawFrame(int row, int frame, float x, float y, float width, float height, int[] light, int filter){
        this.drawFrame(row, frame, x, y, x+width, y+height, 0, 0, this.frameWidth, this.frameHeight, light, filter);
    }

    @Override
    public void drawFrame(int row, int frame, float x, float y, float scale, int filter){
        this.drawFrame(row, frame, x, y, scale, null, filter);
    }

    @Override
    public void drawFrame(int row, int frame, float x, float y, float scale, int[] light, int filter){
        this.drawFrame(row, frame, x, y, this.frameWidth*scale, this.frameHeight*scale, light, filter);
    }

    @Override
    public void drawFrame(int row, int frame, float x1, float y1, float x2, float y2, float srcX1, float srcY1, float srcX2, float srcY2, int[] light, int filter){
        if(frame < 0 || frame >= this.getFrameAmount(row)){
            frame = 0;
        }

        float srcX = frame*this.frameWidth;
        float srcY = row*this.frameHeight;

        this.texture.draw(x1, y1, x2, y2, srcX+srcX1, srcY+srcY1, srcX+srcX2, srcY+srcY2, light, filter);
    }

    @Override
    public void drawRow(int row, float x, float y, float width, float height, int filter){
        this.drawRow(-1L, row, x, y, width, height, null, filter);
    }

    @Override
    public void drawRow(int row, float x, float y, float width, float height, int[] light, int filter){
        this.drawRow(-1L, row, x, y, x+width, y+height, 0, 0, this.frameWidth, this.frameHeight, light, filter);
    }

    @Override
    public void drawRow(int row, float x, float y, float scale, int filter){
        this.drawRow(-1L, row, x, y, scale, null, filter);
    }

    @Override
    public void drawRow(int row, float x, float y, float scale, int[] light, int filter){
        this.drawRow(-1L, row, x, y, this.frameWidth*scale, this.frameHeight*scale, light, filter);
    }

    @Override
    public void drawRow(int row, float x1, float y1, float x2, float y2, float srcX1, float srcY1, float srcX2, float srcY2, int[] light, int filter){

    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x, float y, float width, float height, int filter){
        this.drawRow(startTimeMillis, row, x, y, width, height, null, filter);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x, float y, float width, float height, int[] light, int filter){
        this.drawRow(startTimeMillis, row, x, y, x+width, y+height, 0, 0, this.frameWidth, this.frameHeight, light, filter);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x, float y, float scale, int filter){
        this.drawRow(startTimeMillis, row, x, y, scale, null, filter);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x, float y, float scale, int[] light, int filter){
        this.drawRow(startTimeMillis, row, x, y, this.frameWidth*scale, this.frameHeight*scale, light, filter);
    }

    @Override
    public void drawRow(long startTimeMillis, int row, float x1, float y1, float x2, float y2, float srcX1, float srcY1, float srcX2, float srcY2, int[] light, int filter){
        long time = startTimeMillis > 0 ? Util.getTimeMillis()-startTimeMillis : Util.getTimeMillis();
        this.drawFrame(row, this.getFrameByTime(row, time), x1, y1, x2, y2, srcX1, srcY1, srcX2, srcY2, light, filter);
    }

    @Override
    public int getFrameAmount(int row){
        return this.getRow(row).getFrameAmount();
    }

    @Override
    public long getTotalTime(int row){
        return (long)(this.getRow(row).getTotalTime()*1000F);
    }

    @Override
    public long getFrameTime(int row, int frame){
        return (long)(this.getRow(row).getTime(frame)*1000F);
    }

    private AnimationRow getRow(int row){
        if(row < 0 || row >= this.rows.size()){
            row = 0;
        }

        return this.rows.get(row);
    }

    @Override
    public ITexture getTexture(){
        return this.texture;
    }

    @Override
    public int getFrameWidth(){
        return this.frameWidth;
    }

    @Override
    public int getFrameHeight(){
        return this.frameHeight;
    }

    @Override
    public int getFrameByTime(int row, long millis){
        long runningTime = millis%this.getTotalTime(row);

        int accum = 0;
        for(int i = 0; i < this.getFrameAmount(row); i++){
            accum += this.getFrameTime(row, i);
            if(accum >= runningTime){
                return i;
            }
        }
        return 0;
    }
}
