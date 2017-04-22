package de.ellpeck.rockbottom.world.gen.landscape;

import de.ellpeck.rockbottom.Constants;
import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.gen.IWorldGenerator;

import java.util.Random;

public class WorldGenHollow implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(World world, Chunk chunk, Random rand){
        return chunk.gridY == -15;
    }

    @Override
    public void generate(World world, Chunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                int middleDistY = Math.abs(Constants.CHUNK_SIZE/2-y);
                if(middleDistY <= 3 || rand.nextInt(middleDistY) <= 2){
                    world.setTile(chunk.x+x, chunk.y+y, ContentRegistry.TILE_AIR);
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return 500;
    }
}
