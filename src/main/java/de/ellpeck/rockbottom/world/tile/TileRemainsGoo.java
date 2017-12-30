package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileRemainsGoo extends TileBasic{

    public TileRemainsGoo(){
        super(RockBottomAPI.createInternalRes("remains_goo"));
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.LIQUIDS;
    }

    @Override
    public boolean isLiquid(){
        return true;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public boolean canBreak(IWorld world, int x, int y, TileLayer layer){
        return false;
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(this.fall(world, x, y, layer, true)){
            world.scheduleUpdate(x, y, layer, 10);
        }
    }

    @Override
    public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer, int scheduledMeta){
        this.fall(world, x, y, layer, false);
    }

    private boolean fall(IWorld world, int x, int y, TileLayer layer, boolean simulate){
        if(!world.getState(x, y-1).getTile().isFullTile()){
            if(world.getState(layer, x, y-1).getTile().canReplace(world, x, y-1, layer)){
                if(!simulate){
                    world.setState(layer, x, y-1, world.getState(layer, x, y));
                    world.setState(layer, x, y, GameContent.TILE_AIR.getDefState());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean hasItem(){
        return false;
    }
}
