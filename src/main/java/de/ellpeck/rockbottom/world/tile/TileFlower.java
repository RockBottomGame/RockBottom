package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileFlower extends TileMeta {

    public TileFlower() {
        super(ResourceName.intern("flower"), false);
        this.addSubTile(ResourceName.intern("flower_gray"));
        this.addSubTile(ResourceName.intern("flower_orange"));
        this.addSubTile(ResourceName.intern("flower_pink"));
        this.addSubTile(ResourceName.intern("flower_red"));
        this.addSubTile(ResourceName.intern("flower_white"));
        this.addSubTile(ResourceName.intern("flower_yellow"));
        this.addSubTile(ResourceName.intern("flower_blue"));
        this.addSubTile(ResourceName.intern("flower_purple"));
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return this.canBeHere(world, x, y, layer);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player) {
        return world.isPosLoaded(x, y - 1, false) && this.canBeHere(world, x, y, layer);
    }

    private boolean canBeHere(IWorld world, int x, int y, TileLayer layer) {
        return world.getState(layer, x, y - 1).getTile().canKeepPlants(world, x, y, layer) && world.getState(TileLayer.LIQUIDS, x, y).getTile().isAir();
    }

    @Override
    public boolean canReplace(IWorld world, int x, int y, TileLayer layer) {
        return true;
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer) {
        return null;
    }
}
