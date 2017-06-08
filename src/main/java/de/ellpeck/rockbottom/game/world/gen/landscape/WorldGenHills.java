package de.ellpeck.rockbottom.game.world.gen.landscape;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.ContentRegistry;
import de.ellpeck.rockbottom.game.world.Chunk;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.game.world.World;
import de.ellpeck.rockbottom.game.world.gen.IWorldGenerator;

import java.util.Random;

public class WorldGenHills implements IWorldGenerator{

    private final Random noiseRandom = new Random();

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return chunk.getGridY() == 0;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            float noise = this.getSmoothedNoise(world, chunk.getX(), x);

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

    private float getSmoothedNoise(IWorld world, int chunkX, int x){
        float cornersOut = (this.getNoise(world, chunkX, x+2)+this.getNoise(world, chunkX, x-2))/32F;
        float corners = (this.getNoise(world, chunkX, x+1)+this.getNoise(world, chunkX, x-1))/16F;
        float center = this.getNoise(world, chunkX, x)/8F;

        return cornersOut+corners+center;
    }

    private float getNoise(IWorld world, int chunkX, int x){
        this.noiseRandom.setSeed(chunkX*182+x*238+world.getWorldInfo().seed);
        return this.noiseRandom.nextFloat();
    }

    @Override
    public int getPriority(){
        return 100;
    }
}
