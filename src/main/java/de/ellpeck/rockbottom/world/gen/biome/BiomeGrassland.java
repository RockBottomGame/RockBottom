package de.ellpeck.rockbottom.world.gen.biome;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.biome.BiomeBasic;

import java.util.Random;

public class BiomeGrassland extends BiomeBasic{

    public BiomeGrassland(IResourceName name, int highestY, int lowestY, int weight){
        super(name, highestY, lowestY, weight);
    }

    @Override
    public TileState getState(IWorld world, IChunk chunk, int x, int y, TileLayer layer, INoiseGen noise, Random rand){
        double worldX = chunk.getX()+x;
        int height = (int)(((noise.make2dNoise(worldX/100D, 0D)+noise.make2dNoise(worldX/20D, 0D)*2D)/3D)*10D);

        if(chunk.getY()+y == height){
            return GameContent.TILE_GRASS.getDefState();
        }
        else if(chunk.getY()+y < height){
            return GameContent.TILE_SOIL.getDefState();
        }
        else{
            return GameContent.TILE_AIR.getDefState();
        }
    }
}
