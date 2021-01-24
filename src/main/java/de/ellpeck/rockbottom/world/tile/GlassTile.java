package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class GlassTile extends BasicTile {

    public GlassTile() {
        super(ResourceName.intern("glass"));
    }

    @Override
    public boolean obscuresBackground(IWorld world, int x, int y, TileLayer layer) {
        return false;
    }
}
