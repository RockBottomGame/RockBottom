package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.HashMap;
import java.util.Map;

public class WorldGenBiomes implements IWorldGenerator{

    public static final ResourceName ID = ResourceName.intern("biomes");
    private static final int BIOME_TRANSITION = 5;

    private final Map<Biome, INoiseGen> biomeNoiseGens = new HashMap<>();
    private INoiseGen levelHeightNoise;

    @Override
    public void initWorld(IWorld world){
        this.levelHeightNoise = RockBottomAPI.getApiHandler().makeSimplexNoise(Util.scrambleSeed(1239837, world.getSeed()));
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk){
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            Map<TileLayer, Integer> heights = new HashMap<>();
            for(TileLayer layer : TileLayer.getAllLayers()){
                heights.put(layer, world.getExpectedSurfaceHeight(layer, chunk.getX()+x));
            }

            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                Biome biome = this.getBiome(world, chunk.getX()+x, chunk.getY()+y, heights.get(TileLayer.MAIN));
                chunk.setBiomeInner(x, y, biome);

                INoiseGen noise = this.getBiomeNoise(world, biome);
                for(TileLayer layer : TileLayer.getAllLayers()){
                    chunk.setStateInner(layer, x, y, biome.getState(world, chunk, x, y, layer, noise, heights.get(layer)));
                }
            }
        }
    }

    public Biome getBiome(IWorld world, int x, int y, int height){
        //TODO Make a different biome be returned based on the biome level for that position
        //Everything else is done yay
        return GameContent.BIOME_GRASSLAND;
    }

    public BiomeLevel getSmoothedLevelForPos(IWorld world, int x, int y, int height){
        BiomeLevel level = this.getLevelForPos(world, x, y, height);

        int maxY = level.getMaxY(world, x, y, height);
        if(Math.abs(maxY-y) <= BIOME_TRANSITION){
            int changeHeight = Util.floor(BIOME_TRANSITION*this.levelHeightNoise.make2dNoise(x/10D, maxY));
            if(y >= maxY-changeHeight+Util.ceil(BIOME_TRANSITION/2D)){
                return this.getLevelForPos(world, x, maxY+1, height);
            }
        }
        else{
            int minY = level.getMinY(world, x, y, height);
            if(Math.abs(minY-y) <= BIOME_TRANSITION){
                int changeHeight = Util.ceil(BIOME_TRANSITION*(1D-this.levelHeightNoise.make2dNoise(x/10D, minY)));
                if(y <= minY+changeHeight-Util.floor(BIOME_TRANSITION/2D)){
                    return this.getLevelForPos(world, x, minY-1, height);
                }
            }
        }
        return level;
    }

    private BiomeLevel getLevelForPos(IWorld world, int x, int y, int height){
        BiomeLevel chosen = null;
        for(BiomeLevel level : RockBottomAPI.BIOME_LEVEL_REGISTRY.values()){
            if(y >= level.getMinY(world, x, y, height) && y <= level.getMaxY(world, x, y, height)){
                if(chosen == null || level.getPriority() >= chosen.getPriority()){
                    chosen = level;
                }
            }
        }
        return chosen;
    }

    public INoiseGen getBiomeNoise(IWorld world, Biome biome){
        return this.biomeNoiseGens.computeIfAbsent(biome, b -> RockBottomAPI.getApiHandler().makeSimplexNoise(b.getBiomeSeed(world)));
    }

    @Override
    public int getPriority(){
        return 10000;
    }
}
