package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.*;

public class WorldGenBiomes implements IWorldGenerator{

    public static final IResourceName ID = RockBottomAPI.createInternalRes("biomes");
    private static final int SIZE = 5;
    private static final int MAX_SIZE = 64;
    private final long[] layerSeeds = new long[MAX_SIZE];
    private final Random biomeRandom = new Random();

    private final Map<Biome, INoiseGen> biomeNoiseGens = new HashMap<>();

    @Override
    public void initWorld(IWorld world){
        Random rand = new Random(world.getSeed());
        for(int i = 0; i < MAX_SIZE; i++){
            this.layerSeeds[i] = rand.nextLong();
        }
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk){
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                Biome biome = this.getBiome(chunk.getX()+x, chunk.getY()+y, world);
                chunk.setBiomeInner(x, y, biome);

                INoiseGen noise = this.getBiomeNoise(world, biome);
                for(TileLayer layer : TileLayer.getAllLayers()){
                    chunk.setStateInner(layer, x, y, biome.getState(world, chunk, x, y, layer, noise));
                }
            }
        }
    }

    public Biome getBiome(int x, int y, IWorld world){
        int size = Math.min(MAX_SIZE, SIZE);
        int twoToSize = (int)Math.pow(2, size);

        Pos2 blobPos = this.getBlobPos(x, y, size, world);
        Pos2 perfectBlobPos = new Pos2(blobPos.getX()*twoToSize, blobPos.getY()*twoToSize);

        List<Biome> possibleBiomes = new ArrayList<>();
        int totalWeight = 0;

        for(Biome biome : RockBottomAPI.BIOME_REGISTRY.getUnmodifiable().values()){
            if(perfectBlobPos.getY() >= biome.getLowestY() && perfectBlobPos.getY() <= biome.getHighestY()){
                possibleBiomes.add(biome);
                totalWeight += biome.getWeight();
            }
        }

        this.biomeRandom.setSeed(Util.scrambleSeed(blobPos.getX(), blobPos.getY(), world.getSeed())+world.getSeed());
        int chosenWeight = Util.floor(this.biomeRandom.nextDouble()*(double)totalWeight);

        int weight = 0;
        for(Biome biome : possibleBiomes){
            weight += biome.getWeight();
            if(weight >= chosenWeight){
                return biome;
            }
        }

        return GameContent.BIOME_SKY;
    }

    private Pos2 getBlobPos(int x, int y, int size, IWorld world){
        Pos2 offset = new Pos2(x, y);
        for(int i = 0; i < size; i++){
            offset = this.zoomFromPos(offset, this.layerSeeds[i], world);
        }
        return offset;
    }

    public INoiseGen getBiomeNoise(IWorld world, Biome biome){
       return this.biomeNoiseGens.computeIfAbsent(biome, b -> RockBottomAPI.getApiHandler().makeSimplexNoise(b.getBiomeSeed(world)));
    }

    private Pos2 zoomFromPos(Pos2 pos, long seed, IWorld world){
        boolean xEven = (pos.getX() & 1) == 0;
        boolean yEven = (pos.getY() & 1) == 0;

        int halfX = pos.getX()/2;
        int halfY = pos.getY()/2;

        if(xEven && yEven){
            return new Pos2(halfX, halfY);
        }
        else{
            this.biomeRandom.setSeed(Util.scrambleSeed(pos.getX(), pos.getY(), world.getSeed())+seed);
            int offX = this.biomeRandom.nextBoolean() ? (pos.getX() < 0 ? -1 : 1) : 0;
            int offY = this.biomeRandom.nextBoolean() ? (pos.getY() < 0 ? -1 : 1) : 0;

            if(xEven){
                return new Pos2(halfX, halfY+offY);
            }
            else if(yEven){
                return new Pos2(halfX+offX, halfY);
            }
            else{
                return new Pos2(halfX+offX, halfY+offY);
            }
        }
    }

    @Override
    public int getPriority(){
        return 10000;
    }
}
