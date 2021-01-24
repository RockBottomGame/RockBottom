package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileTallPlant;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.render.tile.CornTileRenderer;

public class CornTile extends TileTallPlant {

    public CornTile() {
        super(ResourceName.intern("corn"));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new CornTileRenderer(name);
    }
}
