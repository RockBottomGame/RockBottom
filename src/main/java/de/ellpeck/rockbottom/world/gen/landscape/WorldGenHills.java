package de.ellpeck.rockbottom.world.gen.landscape;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.apiimpl.SimplexNoise;

import java.util.Random;

public class WorldGenHills implements IWorldGenerator{

    private INoiseGen noiseGen;

    @Override
    public void initWorld(IWorld world, Random rand){
        this.noiseGen = new SimplexNoise(rand);
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return chunk.getGridY() == 0;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            double noise = this.getSmoothedNoise(chunk.getX(), x);

            int height = (int)(noise*5);
            for(int y = 0; y <= height; y++){
                chunk.setTileInner(x, y, y == height ? GameContent.TILE_GRASS : GameContent.TILE_DIRT);
            }

            int backgroundHeight = (int)(noise*4);
            for(int y = 0; y < backgroundHeight; y++){
                chunk.setTileInner(TileLayer.BACKGROUND, x, y, GameContent.TILE_DIRT);
            }
        }
    }

    private double getSmoothedNoise(int chunkX, int x){
        double cornersOut = (this.getNoise(chunkX, x+2)+this.getNoise(chunkX, x-2))/2F;
        double corners = (this.getNoise(chunkX, x+1)+this.getNoise(chunkX, x-1));
        double center = this.getNoise(chunkX, x);

        return cornersOut+corners+center;
    }

    private double getNoise(int chunkX, int x){
        return (this.noiseGen.make2dNoise(chunkX, x)+1)/2;
    }

    @Override
    public int getPriority(){
        return 100;
    }
}
