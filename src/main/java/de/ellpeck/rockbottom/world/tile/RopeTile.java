package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.TileItem;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.item.RopeItem;
import de.ellpeck.rockbottom.render.tile.RopeTileRenderer;

import java.util.List;

public class RopeTile extends BasicTile {
    public RopeTile(ResourceName name) {
        super(name);
        this.addProps(StaticTileProps.IS_ENDING);
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(manager.localize(ResourceName.intern("info." + this.getName().getResourceName())));
    }

    @Override
    public boolean canClimb(IWorld world, int x, int y, TileLayer layer, TileState state, BoundingBox entityBox, BoundingBox entityBoxMotion, List<BoundingBox> tileBoxes, Entity entity) {
        float climbThreshold = state.get(StaticTileProps.IS_ENDING) ? 0.35f : 0;
        return (entity.getY() - y) > climbThreshold && entity.getX() >= (x + 0.25F) && entity.getX() <= (x + 0.75F);
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
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        super.onChangeAround(world, x, y, layer, changedX, changedY, changedLayer);

        TileState state = world.getState(layer, x, y);
        if (state.getTile() == this) {
            world.setState(layer, x, y, state.prop(StaticTileProps.IS_ENDING, world.getState(layer, x, y - 1).getTile() != this));
        }
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractPlayerEntity placer) {
        return this.getDefState().prop(StaticTileProps.IS_ENDING, world.getState(layer, x, y - 1).getTile() != this);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
        return this.isValidRopePos(world, x, y);
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return this.isValidRopePos(world, x, y);
    }

    @Override
    protected TileItem createItemTile() {
        return new RopeItem(ResourceName.intern("plant_rope"));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new RopeTileRenderer(name, true);
    }

    private boolean isValidRopePos(IWorld world, int x, int y) {
        Tile above = world.isPosLoaded(x, y + 1) ? world.getState(x, y + 1).getTile() : this;
        return above == this || above.isFullTile();
    }
}
