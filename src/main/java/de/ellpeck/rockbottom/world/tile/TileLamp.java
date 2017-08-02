package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;

public class TileLamp extends TileTorch{

    public TileLamp(IResourceName name){
        super(name);
    }

    @Override
    public int getLight(IWorld world, int x, int y, TileLayer layer){
        return 30;
    }
}
