package de.ellpeck.rockbottom.world.gen.biome;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.biome.BiomeBasic;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class BiomeSky extends BiomeBasic{

    public BiomeSky(ResourceName name, int highestY, int weight){
        super(name, highestY, 0, weight);
    }

    @Override
    public TileState getState(IWorld world, IChunk chunk, int x, int y, TileLayer layer, INoiseGen noise, int surfaceHeight){
        return GameContent.TILE_AIR.getDefState();
    }

    @Override
    public int getLowestY(IWorld world, int x, int y){
        return world.getExpectedSurfaceHeight(TileLayer.MAIN, x)+30;
    }
}
