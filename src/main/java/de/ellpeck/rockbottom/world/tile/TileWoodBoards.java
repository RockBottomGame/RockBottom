package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.tile.TileMeta;

public class TileWoodBoards extends TileMeta{

    public TileWoodBoards(){
        super(RockBottomAPI.createInternalRes("wood_boards"));
        this.addSubTile(RockBottomAPI.createInternalRes("wood_boards_old"));
    }
}
