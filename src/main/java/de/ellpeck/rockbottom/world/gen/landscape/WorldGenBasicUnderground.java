package de.ellpeck.rockbottom.world.gen.landscape;

import de.ellpeck.rockbottom.Constants;
import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.world.tile.Tile;

import java.util.Random;

public class WorldGenBasicUnderground implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(World world, Chunk chunk, Random rand){
        return chunk.gridY < 0;
    }

    @Override
    public void generate(World world, Chunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                for(TileLayer layer : TileLayer.LAYERS){
                    Tile tile;

                    if(chunk.gridY == -1 && rand.nextInt(y+1) >= 8){
                        tile = ContentRegistry.TILE_DIRT;
                    }
                    else{
                        tile = ContentRegistry.TILE_ROCK;
                    }

                    chunk.setTileInner(layer, x, y, tile);
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return 80;
    }
}
