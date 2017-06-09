package de.ellpeck.rockbottom.game.world.gen.landscape;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.ContentRegistry;
import de.ellpeck.rockbottom.game.world.gen.IWorldGenerator;

import java.util.Random;

public class WorldGenHollow implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return chunk.getGridY() == -15;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                int middleDistY = Math.abs(Constants.CHUNK_SIZE/2-y);
                if(middleDistY <= 3 || rand.nextInt(middleDistY) <= 2){
                    world.setTile(chunk.getX()+x, chunk.getY()+y, ContentRegistry.TILE_AIR);
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return 500;
    }
}
