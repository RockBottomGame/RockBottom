package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class WorldGenPebbles implements IWorldGenerator{

    private INoiseGen pebbleNoise;

    @Override
    public void initWorld(IWorld world, Random rand){
        this.pebbleNoise = RockBottomAPI.getApiHandler().makeSimplexNoise(rand);
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            int y = chunk.getLowestAirUpwardsInner(TileLayer.MAIN, x, 0, true);
            if(y >= 0){
                float chance = chunk.getBiomeInner(x, y).getPebbleChance();
                if(chance > 0F && this.pebbleNoise.make2dNoise(x/2D, y/2D) <= chance){
                    Tile tile = GameContent.TILE_PEBBLES;
                    if(tile.canPlace(world, chunk.getX()+x, chunk.getY()+y, TileLayer.MAIN)){
                        chunk.setStateInner(x, y, tile.getDefState());
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
