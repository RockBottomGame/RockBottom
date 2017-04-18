package de.ellpeck.game.world.gen.landscape;

import de.ellpeck.game.Constants;
import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.gen.IWorldGenerator;

import java.util.Random;

public class WorldGenDebugLandscape implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(World world, Chunk chunk){
        return false;
    }

    @Override
    public void generate(World world, Chunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                if(chunk.y+y == 15){
                    chunk.setTileInner(x, y, ContentRegistry.TILE_GRASS);
                }
                else if(chunk.y+y < 15){
                    chunk.setTileInner(x, y, rand.nextFloat() <= 0.75 ? ContentRegistry.TILE_DIRT : ContentRegistry.TILE_ROCK);
                    chunk.setTileInner(TileLayer.BACKGROUND, x, y, rand.nextFloat() <= 0.75 ? ContentRegistry.TILE_DIRT : ContentRegistry.TILE_ROCK);
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return Integer.MAX_VALUE;
    }
}
