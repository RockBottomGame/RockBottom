package de.ellpeck.rockbottom.world.gen.ore;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.world.gen.WorldGenOre;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;

import java.util.Collections;
import java.util.Set;

public class WorldGenCoal extends WorldGenOre{

    @Override
    public int getPriority(){
        return 0;
    }

    @Override
    protected Set<Biome> getAllowedBiomes(){
        return Collections.singleton(GameContent.BIOME_UNDERGROUND);
    }

    @Override
    protected int getMaxAmount(){
        return 5;
    }

    @Override
    protected int getClusterRadiusX(){
        return 6;
    }

    @Override
    protected int getClusterRadiusY(){
        return 4;
    }

    @Override
    protected TileState getOreState(){
        return GameContent.TILE_COAL.getDefState();
    }

    @Override
    protected int getHighestGridPos(){
        return 0;
    }
}
