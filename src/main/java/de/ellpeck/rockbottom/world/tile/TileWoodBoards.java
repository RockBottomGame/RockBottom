package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class TileWoodBoards extends TileMeta {

    public TileWoodBoards() {
        super(ResourceName.intern("wood_boards"));
        this.addSubTile(ResourceName.intern("wood_boards_old"));
    }
}
