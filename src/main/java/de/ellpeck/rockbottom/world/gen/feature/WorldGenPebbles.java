package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class WorldGenPebbles implements IWorldGenerator{

    private final Random pebbleRandom = new Random();

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk){
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            int y = chunk.getHeightInner(TileLayer.MAIN, x);
            if(chunk.getY()+y >= world.getExpectedSurfaceHeight(TileLayer.MAIN, chunk.getX()+x)){
                if(y < Constants.CHUNK_SIZE && chunk.getStateInner(x, y).getTile().canReplace(world, chunk.getX()+x, chunk.getY()+y, TileLayer.MAIN)){
                    float chance = chunk.getBiomeInner(x, y).getPebbleChance();

                    this.pebbleRandom.setSeed(Util.scrambleSeed(x, y, world.getSeed()));
                    if(chance > 0F && this.pebbleRandom.nextFloat() <= chance){
                        Tile tile = GameContent.TILE_PEBBLES;
                        if(tile.canPlace(world, chunk.getX()+x, chunk.getY()+y, TileLayer.MAIN, null)){
                            chunk.setStateInner(x, y, tile.getDefState());
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return -90;
    }
}
