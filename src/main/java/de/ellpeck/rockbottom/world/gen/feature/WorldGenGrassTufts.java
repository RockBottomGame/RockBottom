package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.world.tile.TileGrassTuft;

import java.util.Random;

public class WorldGenGrassTufts implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return chunk.getGridY() == 0;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        int amount = rand.nextInt(20)+10;
        for(int i = 0; i < amount; i++){
            int x = rand.nextInt(Constants.CHUNK_SIZE);
            int y = chunk.getLowestAirUpwards(TileLayer.MAIN, chunk.getX()+x, 0);

            if(y > 0 && chunk.getStateInner(x, y-1).getTile().isFullTile()){
                TileMeta tile = (TileMeta)GameContent.TILE_GRASS_TUFT;
                chunk.setStateInner(x, y, tile.getDefState().prop(tile.metaProp, Util.RANDOM.nextInt(tile.metaProp.getVariants())));
            }
        }
    }

    @Override
    public int getPriority(){
        return 500;
    }
}
