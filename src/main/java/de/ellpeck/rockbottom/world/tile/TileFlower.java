package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileFlower extends TileMeta{

    public TileFlower(){
        super(RockBottomAPI.createInternalRes("flower"), false);
        this.addSubTile(RockBottomAPI.createInternalRes("flower_gray"));
        this.addSubTile(RockBottomAPI.createInternalRes("flower_orange"));
        this.addSubTile(RockBottomAPI.createInternalRes("flower_pink"));
        this.addSubTile(RockBottomAPI.createInternalRes("flower_red"));
        this.addSubTile(RockBottomAPI.createInternalRes("flower_white"));
        this.addSubTile(RockBottomAPI.createInternalRes("flower_yellow"));
        this.addSubTile(RockBottomAPI.createInternalRes("flower_blue"));
        this.addSubTile(RockBottomAPI.createInternalRes("flower_purple"));
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        return world.getState(layer, x, y-1).getTile().canKeepPlants(world, x, y, layer);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player){
        return world.isPosLoaded(x, y-1) && world.getState(layer, x, y-1).getTile().canKeepPlants(world, x, y, layer);
    }

    @Override
    public boolean canReplace(IWorld world, int x, int y, TileLayer layer){
        return true;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer){
        return null;
    }
}
