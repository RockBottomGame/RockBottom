package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.world.IWorld;

public class TileDirt extends TileBasic{

    public TileDirt(){
        super(RockBottom.internalRes("dirt"));
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y){
        if(world.isPosLoaded(x, y+1) && !world.getTile(x, y+1).isFullTile()){
            world.setTile(x, y, GameContent.TILE_GRASS);
        }
    }
}
