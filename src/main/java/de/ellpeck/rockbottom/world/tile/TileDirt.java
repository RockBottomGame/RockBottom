package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.init.AbstractGame;

public class TileDirt extends TileBasic{

    public TileDirt(){
        super(AbstractGame.internalRes("dirt"));
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y){
        if(world.isDaytime() && Util.RANDOM.nextInt(20) <= 0){
            if(world.isPosLoaded(x, y+1) && !world.getState(x, y+1).getTile().isFullTile() && world.getSkyLight(x, y+1) >= 25){
                world.setState(x, y, GameContent.TILE_GRASS.getDefState());
            }
        }
    }
}
