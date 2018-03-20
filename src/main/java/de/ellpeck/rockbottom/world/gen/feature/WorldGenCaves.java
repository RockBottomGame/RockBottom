package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;

public class WorldGenCaves implements IWorldGenerator{

    private INoiseGen noiseGen;

    @Override
    public void initWorld(IWorld world){
        this.noiseGen = RockBottomAPI.getApiHandler().makeSimplexNoise(world.getSeed());
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk){
        return false; //TODO Reenable this when caves get fixed
    }

    @Override
    public void generate(IWorld world, IChunk chunk){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                double noise = this.noiseGen.make2dNoise((double)(chunk.getX()+x)/45D, (double)(chunk.getY()+y)/15D);
                if(noise <= 0.25D){
                    chunk.setStateInner(x, y, GameContent.TILE_AIR.getDefState());
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return 1000;
    }
}
