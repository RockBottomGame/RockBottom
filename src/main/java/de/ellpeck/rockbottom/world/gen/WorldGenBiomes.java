package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;

import java.util.*;

public class WorldGenBiomes implements IWorldGenerator{

    private INoiseGen biomeNoise;
    private INoiseGen stateNoise;

    @Override
    public void initWorld(IWorld world, Random rand){
        this.biomeNoise = RockBottomAPI.getApiHandler().makeSimplexNoise(rand);
        this.stateNoise = RockBottomAPI.getApiHandler().makeSimplexNoise(rand);
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                //TODO Randomize biome based on noise, weight, highest/lowest grid positions
                Biome biome = GameContent.BIOME_SKY;
                chunk.setBiomeInner(x, y, biome);

                if(chunk.getStateInner(x, y) != GameContent.TILE_GRASS.getDefState()){
                    for(TileLayer layer : TileLayer.LAYERS){
                        chunk.setStateInner(layer, x, y, biome.getState(world, chunk, x, y, layer, this.stateNoise, rand));
                    }
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return 0;
    }
}
