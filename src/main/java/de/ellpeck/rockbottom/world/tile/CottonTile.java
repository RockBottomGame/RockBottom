package de.ellpeck.rockbottom.world.tile;


import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileTallPlant;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.render.tile.CottonTileRenderer;

public class CottonTile extends TileTallPlant {
    public CottonTile() {
        super(ResourceName.intern("cotton"));
    }

    @Override
    protected final ITileRenderer createRenderer(ResourceName name) {
        return new CottonTileRenderer(name);
    }
}
