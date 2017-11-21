package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Collections;
import java.util.List;

public class TileGrassTuft extends TileMeta{

    public TileGrassTuft(){
        super(RockBottomAPI.createInternalRes("grass_tuft"));
        this.addSubTile(RockBottomAPI.createInternalRes("grass_short"));
        this.addSubTile(RockBottomAPI.createInternalRes("grass_tall"));
        this.addSubTile(RockBottomAPI.createInternalRes("bush"));
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer){
        return Collections.emptyList();
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
        return world.isPosLoaded(x, y-1) && world.getState(layer, x, y-1).getTile().canKeepPlants(world, x, y, layer);
    }

    @Override
    public boolean canReplace(IWorld world, int x, int y, TileLayer layer){
        return true;
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        return world.getState(layer, x, y-1).getTile().canKeepPlants(world, x, y, layer);
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}
