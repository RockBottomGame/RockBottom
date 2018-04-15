package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class WorldGenHeights implements IWorldGenerator{

    public static final ResourceName ID = ResourceName.intern("heights");
    private INoiseGen noiseGen;

    @Override
    public void initWorld(IWorld world){
        this.noiseGen = RockBottomAPI.getApiHandler().makeSimplexNoise(world.getSeed());
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk){
        return false;
    }

    @Override
    public void generate(IWorld world, IChunk chunk){

    }

    @Override
    public int getPriority(){
        return 15000;
    }

    public int getHeight(TileLayer layer, int x){
        return getHeight(layer, x, this.noiseGen, 0, 10);
    }

    public static int getHeight(TileLayer layer, int x, INoiseGen noiseGen, int minHeight, int maxHeight){
        double noise = noiseGen.make2dNoise(x/100D, 0D);
        noise += noiseGen.make2dNoise(x/20D, 0D)*2D;

        int height = (int)(noise/3.5D*(double)(maxHeight-minHeight))+minHeight;

        if(layer == TileLayer.BACKGROUND){
            height -= Util.ceil(noiseGen.make2dNoise(x/10D, 0D)*3D);
        }

        return height;
    }
}
