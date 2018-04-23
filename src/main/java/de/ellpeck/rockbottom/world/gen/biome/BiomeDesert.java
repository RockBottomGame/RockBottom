package de.ellpeck.rockbottom.world.gen.biome;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.biome.BiomeBasic;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class BiomeDesert extends BiomeBasic{

    public BiomeDesert(ResourceName name, BiomeLevel... levels){
        super(name, levels);
    }

    @Override
    public TileState getState(IWorld world, IChunk chunk, int x, int y, TileLayer layer, INoiseGen noise, int surfaceHeight){
        if(layer == TileLayer.MAIN || layer == TileLayer.BACKGROUND){
            double worldX = chunk.getX()+x;

            if(layer == TileLayer.BACKGROUND){
                surfaceHeight -= Util.ceil(noise.make2dNoise(worldX/10D, 0D)*3D);
            }

            if(chunk.getY()+y <= surfaceHeight){
                if(chunk.getY()+y >= surfaceHeight-Util.ceil(noise.make2dNoise(worldX/5D, 0D)*3D)){
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
    public TileState getFillerTile(IWorld world, IChunk chunk, int x, int y){
        return GameContent.TILE_SAND.getDefState();
    }
}
