package de.ellpeck.rockbottom.world.gen.landscape;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;

import java.util.Random;

public class WorldGenDebugLandscape implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return false;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                if(chunk.getY()+y == 15){
                    chunk.setTileInner(x, y, GameContent.TILE_GRASS);
                }
                else if(chunk.getY()+y < 15){
                    chunk.setTileInner(x, y, rand.nextFloat() <= 0.75 ? GameContent.TILE_DIRT : GameContent.TILE_ROCK);
                    chunk.setTileInner(TileLayer.BACKGROUND, x, y, rand.nextFloat() <= 0.75 ? GameContent.TILE_DIRT : GameContent.TILE_ROCK);
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return Integer.MAX_VALUE;
    }
}
