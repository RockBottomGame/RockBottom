package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.List;

public class RopeTile extends BasicTile {
    public RopeTile(ResourceName name) {
        super(name);
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(manager.localize(ResourceName.intern("info." + this.getName().getResourceName())));
    }

    @Override
    public boolean canClimb(IWorld world, int x, int y, TileLayer layer, TileState state, BoundingBox entityBox, BoundingBox entityBoxMotion, List<BoundingBox> tileBoxes, Entity entity) {
        return Util.floor(entity.getY()) == y;
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
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
        return this.isRopePos(world, x, y);
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return this.isRopePos(world, x, y);
    }

    private boolean isRopePos(IWorld world, int x, int y) {
        Tile above = world.isPosLoaded(x, y + 1) ? world.getState(x, y + 1).getTile() : this;
        return above == this || above.isFullTile();
    }
}
