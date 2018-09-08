package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileGlass extends TileBasic {

    public TileGlass() {
        super(ResourceName.intern("glass"));
    }

    @Override
    public boolean obscuresBackground(IWorld world, int x, int y, TileLayer layer) {
        return false;
    }
}
