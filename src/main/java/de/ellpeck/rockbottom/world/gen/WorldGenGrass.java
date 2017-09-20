package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class WorldGenGrass implements IWorldGenerator{

    private INoiseGen grassNoise;

    @Override
    public void initWorld(IWorld world, Random rand){
        this.grassNoise = RockBottomAPI.getApiHandler().makeSimplexNoise(rand);
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            int y = chunk.getLowestAirUpwardsInner(TileLayer.MAIN, x, 0);
            if(y >= 0 && chunk.getBiomeInner(x, y).hasGrasslandDecoration()){
                if(this.grassNoise.make2dNoise(x/4D, 0) >= 0.5){
                    TileMeta tile = (TileMeta)GameContent.TILE_GRASS_TUFT;
                    if(tile.canPlace(world, chunk.getX()+x, chunk.getY()+y, TileLayer.MAIN)){
                        int type = Util.floor(this.grassNoise.make2dNoise(x, 0)*(double)tile.metaProp.getVariants());
                        chunk.setStateInner(x, y, tile.getDefState().prop(tile.metaProp, type));
                    }
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return -100;
    }
}
