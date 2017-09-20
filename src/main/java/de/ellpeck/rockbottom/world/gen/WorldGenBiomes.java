package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        List<Biome> possibleBiomes = new ArrayList<>();

        for(Biome biome : RockBottomAPI.BIOME_REGISTRY.getUnmodifiable().values()){
            if(chunk.getGridY() >= biome.getLowestGridPos() && chunk.getGridY() <= biome.getHighestGridPos()){
                possibleBiomes.add(biome);
            }
        }


        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                //TODO Work out a way to make biomes generate randomly properly
                Biome biome = possibleBiomes.get(0);
                chunk.setBiomeInner(x, y, biome);

                for(TileLayer layer : TileLayer.getAllLayers()){
                    chunk.setStateInner(layer, x, y, biome.getState(world, chunk, x, y, layer, this.stateNoise, rand));
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return 10000;
    }
}
