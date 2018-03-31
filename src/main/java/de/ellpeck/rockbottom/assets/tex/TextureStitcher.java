package de.ellpeck.rockbottom.assets.tex;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.assets.texture.ImageBuffer;
import de.ellpeck.rockbottom.api.assets.texture.stitcher.IStitchCallback;
import de.ellpeck.rockbottom.api.assets.texture.stitcher.ITextureStitcher;
import de.ellpeck.rockbottom.api.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TextureStitcher implements ITextureStitcher{

    private final List<Instruction> texturesToStitch = new ArrayList<>();

    public void doStitch(){
        List<StitchPosition> stitchPositions = new ArrayList<>();

        int highestWidth = 0;
        int lowestRowHeight = 0;
        int endX = 0;
        int endY = 0;

        for(Instruction instruction : this.texturesToStitch){
            try{
                Texture tex = instruction.getTexture();

                int x = 0;
                int y = 0;

                while(true){
                    int index = this.getStitchPosition(x, y, tex.getTextureWidth()+2, tex.getTextureHeight()+2, stitchPositions);
                    if(index != -1){
                        StitchPosition pos = stitchPositions.get(index);

                        int height = pos.texture.getTextureHeight()+2;
                        if(lowestRowHeight == 0 || height < lowestRowHeight){
                            lowestRowHeight = height;
                        }

                        x += pos.texture.getTextureWidth()+2;
                        if(x+tex.getTextureWidth() >= Math.max(1024, highestWidth)){
                            x = 0;
                            y += lowestRowHeight;
                            lowestRowHeight = 0;
                        }
                    }
                    else{
                        break;
                    }
                }

                stitchPositions.add(new StitchPosition(tex, instruction, x+1, y+1));
                RockBottomAPI.logger().finer("Found stitch position for "+instruction.refName+" at "+(x+1)+", "+(y+1)+" with dimensions "+tex.getTextureWidth()+'x'+tex.getTextureHeight());

                highestWidth = Math.max(highestWidth, tex.getTextureWidth());
                endX = Math.max(endX, x+tex.getTextureWidth());
                endY = Math.max(endY, y+tex.getTextureHeight());
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't stitch texture "+instruction.refName, e);
            }
        }

        ImageBuffer buffer = new ImageBuffer(endX+2, endY+2);
        RockBottomAPI.logger().info("Creating stitched texture with "+endX+", "+endY+" pixels");

        for(StitchPosition position : stitchPositions){
            try{
                ByteBuffer data = position.texture.getPixelData();
                int width = position.texture.getTextureWidth();
                int height = position.texture.getTextureHeight();

                for(int subX = -1; subX <= width; subX++){
                    for(int subY = -1; subY <= height; subY++){
                        int offset = (Util.clamp(subX, 0, width-1)+(Util.clamp(subY, 0, height-1)*width))*4;

                        buffer.setRGBA(position.x+subX, position.y+subY, data.get(offset), data.get(offset+1), data.get(offset+2), data.get(offset+3));
                    }
                }

                position.texture.dispose();
                RockBottomAPI.logger().finer("Successfully stitched "+position.instruction.refName+" to "+position.x+", "+position.y);
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.WARNING, "Failed to stitch texture "+position.instruction.refName, e);
            }
        }

        ITexture textureMap;
        try{
            textureMap = new Texture(buffer.getWidth(), buffer.getHeight(), buffer.getRGBA());
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Couldn't load stitched texture", e);
            return;
        }

        for(StitchPosition position : stitchPositions){
            try{
                ITexture texture = textureMap.getSubTexture(position.x, position.y, position.texture.getTextureWidth(), position.texture.getTextureHeight());
                position.instruction.callback.onStitched(position.x, position.y, texture);

                RockBottomAPI.logger().finer("Finalized stitching of "+position.instruction.refName+" with final sub texture at "+position.x+", "+position.y+" with dimensions "+position.texture.getTextureWidth()+'x'+position.texture.getTextureHeight());
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't finalize stitching of texture "+position.instruction.refName, e);
            }
        }

        if(Main.saveTextureSheet){
            try{
                RockBottomAPI.logger().info("Writing texture sheet to file...");

                BufferedImage image = new BufferedImage(buffer.getWidth(), buffer.getHeight(), BufferedImage.TYPE_INT_ARGB);

                for(int x = 0; x < buffer.getWidth(); x++){
                    for(int y = 0; y < buffer.getHeight(); y++){
                        int color = textureMap.getTextureColor(x, y);
                        image.setRGB(x, y, color);
                    }
                }

                File file = new File(RockBottomAPI.getGame().getDataManager().getGameDir(), "texture_sheet.png");
                ImageIO.write(image, "png", file);
                RockBottomAPI.logger().info("Wrote texture sheet to file at "+file);
            }
            catch(IOException e){
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't write texture sheet to file", e);
            }
        }
    }

    private int getStitchPosition(int x, int y, int width, int height, List<StitchPosition> positions){
        for(int i = 0; i < positions.size(); i++){
            StitchPosition position = positions.get(i);
            if(x <= position.x+position.texture.getTextureWidth() && x+width >= position.x && y <= position.y+position.texture.getTextureHeight() && y+height >= position.y){
                return i;
            }
        }
        return -1;
    }

    public void reset(){
        this.texturesToStitch.clear();
    }

    @Override
    public void loadTexture(String refName, InputStream stream, IStitchCallback callback){
        this.texturesToStitch.add(new Instruction(refName, stream, callback));
    }

    @Override
    public void loadTexture(String refName, ImageBuffer data, IStitchCallback callback){
        this.texturesToStitch.add(new DataInstruction(refName, data, callback));
    }

    private static class Instruction{

        protected final String refName;
        protected final InputStream stream;
        protected final IStitchCallback callback;

        protected Instruction(String refName, InputStream stream, IStitchCallback callback){
            this.refName = refName;
            this.stream = stream;
            this.callback = callback;
        }

        protected Texture getTexture() throws Exception{
            Texture tex = new Texture(this.stream);
            this.stream.close();
            return tex;
        }
    }

    private static class DataInstruction extends Instruction{

        protected final ImageBuffer data;

        protected DataInstruction(String refName, ImageBuffer data, IStitchCallback callback){
            super(refName, null, callback);
            this.data = data;
        }

        @Override
        protected Texture getTexture(){
            return new Texture(this.data.getWidth(), this.data.getHeight(), this.data.getRGBA());
        }
    }

    private static class StitchPosition{

        protected final Texture texture;
        protected final Instruction instruction;
        protected final int x;
        protected final int y;

        protected StitchPosition(Texture texture, Instruction instruction, int x, int y){
            this.texture = texture;
            this.instruction = instruction;
            this.x = x;
            this.y = y;
        }
    }
}