package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.tile.TileBasic;

public class TileSand extends TileBasic{

    public TileSand(){
        super(RockBottomAPI.createInternalRes("sand"));
    }
}
