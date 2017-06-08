package de.ellpeck.rockbottom.game.world.gen.landscape;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.ContentRegistry;
import de.ellpeck.rockbottom.game.world.Chunk;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.game.world.World;
import de.ellpeck.rockbottom.game.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.tile.Tile;

import java.util.Random;

public class WorldGenBasicUnderground implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return chunk.getGridY() < 0;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                for(TileLayer layer : TileLayer.LAYERS){
                    Tile tile;

                    if(chunk.getGridY() == -1 && rand.nextInt(y+1) >= 8){
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
