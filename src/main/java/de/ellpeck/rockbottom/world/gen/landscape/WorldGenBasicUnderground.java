package de.ellpeck.rockbottom.world.gen.landscape;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;

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
                        tile = GameContent.TILE_DIRT;
                    }
                    else{
                        tile = GameContent.TILE_ROCK;
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
