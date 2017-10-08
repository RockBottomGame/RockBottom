package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldGenBiomes implements IWorldGenerator{

    private final Map<Biome, INoiseGen> biomeNoise = new HashMap<>();

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk){
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk){
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

                INoiseGen noise = this.biomeNoise.computeIfAbsent(biome, b -> RockBottomAPI.getApiHandler().makeSimplexNoise(Util.scrambleSeed(b.getName().hashCode(), world.getSeed())));

                for(TileLayer layer : TileLayer.getAllLayers()){
                    chunk.setStateInner(layer, x, y, biome.getState(world, chunk, x, y, layer, noise));
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return 10000;
    }
}
