package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class WoodBoardsTile extends TileMeta {

    public WoodBoardsTile() {
        super(ResourceName.intern("wood_boards"));
        this.addSubTile(ResourceName.intern("wood_boards_old"));
    }
}
