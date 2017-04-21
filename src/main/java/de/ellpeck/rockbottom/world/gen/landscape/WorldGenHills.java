package de.ellpeck.rockbottom.world.gen.landscape;

import de.ellpeck.rockbottom.Constants;
import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.gen.IWorldGenerator;

import java.util.Random;

public class WorldGenHills implements IWorldGenerator{

    private final Random noiseRandom = new Random();

    @Override
    public boolean shouldGenerate(World world, Chunk chunk, Random rand){
        return chunk.gridY == 0;
    }

    @Override
    public void generate(World world, Chunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            float noise = this.getSmoothedNoise(world, chunk.x, x);

            int height = (int)(noise*30);
            for(int y = 0; y <= height; y++){
                chunk.setTileInner(x, y, y == height ? ContentRegistry.TILE_GRASS : ContentRegistry.TILE_DIRT);
            }

            int backgroundHeight = (int)(noise*28);
            for(int y = 0; y < backgroundHeight; y++){
                chunk.setTileInner(TileLayer.BACKGROUND, x, y, ContentRegistry.TILE_DIRT);
            }
        }
    }

    private float getSmoothedNoise(World world, int chunkX, int x){
        float cornersOut = (this.getNoise(world, chunkX, x+2)+this.getNoise(world, chunkX, x-2))/32F;
        float corners = (this.getNoise(world, chunkX, x+1)+this.getNoise(world, chunkX, x-1))/16F;
        float center = this.getNoise(world, chunkX, x)/8F;

        return cornersOut+corners+center;
    }

    private float getNoise(World world, int chunkX, int x){
        this.noiseRandom.setSeed(chunkX*182+x*238+world.info.seed);
        return this.noiseRandom.nextFloat();
    }

    @Override
    public int getPriority(){
        return 100;
    }
}
