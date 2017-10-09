package de.ellpeck.rockbottom.world.gen.biome;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.biome.BiomeBasic;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class BiomeDesert extends BiomeBasic{

    public BiomeDesert(IResourceName name, int highestY, int lowestY, int weight){
        super(name, highestY, lowestY, weight);
    }

    @Override
    public TileState getState(IWorld world, IChunk chunk, int x, int y, TileLayer layer, INoiseGen noise){
        if(layer == TileLayer.MAIN || layer == TileLayer.BACKGROUND){
            double worldX = chunk.getX()+x;
            int height = (int)(((noise.make2dNoise(worldX/100D, 0D)+noise.make2dNoise(worldX/20D, 0D)*2D)/3D)*10D);

            if(layer == TileLayer.BACKGROUND){
                height -= Util.ceil(noise.make2dNoise(worldX/10D, 0D)*3D);
            }

            if(chunk.getY()+y <= height){
                if(chunk.getY()+y >= height-Util.ceil(noise.make2dNoise(worldX/5D, 0D)*3D)){
                    return GameContent.TILE_SAND.getDefState();
                }
                else{
                    return GameContent.TILE_SANDSTONE.getDefState();
                }
            }
        }
        return GameContent.TILE_AIR.getDefState();
    }

    @Override
    public float getPebbleChance(){
        return 0.35F;
    }

    @Override
    public int getNoiseSeedModifier(IWorld world){
        return 23872;
    }
}
