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
            double noise = this.noiseGen.make2dNoise((double)(chunk.getX()+x)/10, 0);

            int height = (int)(noise*3);
            for(int y = 0; y <= height; y++){
                chunk.setStateInner(x, y, (y == height ? GameContent.TILE_GRASS : GameContent.TILE_DIRT).getDefState());
            }

            int backgroundHeight = (int)(noise*2);
            for(int y = 0; y < backgroundHeight; y++){
                chunk.setStateInner(TileLayer.BACKGROUND, x, y, GameContent.TILE_DIRT.getDefState());
            }
        }
    }

    @Override
    public int getPriority(){
        return 100;
    }
}
