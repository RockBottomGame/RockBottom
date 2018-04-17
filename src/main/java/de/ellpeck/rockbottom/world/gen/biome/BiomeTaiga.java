package de.ellpeck.rockbottom.world.gen.biome;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.biome.BiomeBasic;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class BiomeTaiga extends BiomeBasic{

    public BiomeTaiga(ResourceName name, int highestY, int lowestY, int weight){
        super(name, highestY, lowestY, weight);
    }

    @Override
    public TileState getState(IWorld world, IChunk chunk, int x, int y, TileLayer layer, INoiseGen noise, int surfaceHeight){
        int theX = chunk.getX()+x;
        int theY = chunk.getY()+y;
        int snowHeight = surfaceHeight+Util.ceil(noise.make2dNoise(theX/30D, 0D)*3D);

        if(theY <= snowHeight && theY > surfaceHeight && (layer == TileLayer.MAIN || layer == TileLayer.BACKGROUND) && world.getExpectedBiome(theX, theY-1) == this){
            return GameContent.TILE_SNOW.getDefState();
        }
        else{
            int stoneHeight = surfaceHeight-Util.ceil(noise.make2dNoise(theX/5D, 0D)*3D)-2;
            return BiomeGrassland.getState(layer, theY, surfaceHeight, stoneHeight);
        }
    }
}
