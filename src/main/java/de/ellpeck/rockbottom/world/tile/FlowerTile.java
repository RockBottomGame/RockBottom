package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.IPotPlantable;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class FlowerTile extends TileMeta implements IPotPlantable {

    public FlowerTile() {
        super(ResourceName.intern("flower"), false);
        this.addSubTile(ResourceName.intern("gray_flower"));
        this.addSubTile(ResourceName.intern("orange_flower"));
        this.addSubTile(ResourceName.intern("pink_flower"));
        this.addSubTile(ResourceName.intern("red_flower"));
        this.addSubTile(ResourceName.intern("white_flower"));
        this.addSubTile(ResourceName.intern("yellow_flower"));
        this.addSubTile(ResourceName.intern("blue_flower"));
        this.addSubTile(ResourceName.intern("purple_flower"));
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return this.canBeHere(world, x, y, layer);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
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
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    public float getRenderYOffset(IWorld world, TileState pot, int x, int y, ItemInstance item) {
        return -3/12f;
    }
}
