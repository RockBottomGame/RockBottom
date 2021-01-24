package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.LampTileRenderer;

public class LampTile extends BasicTile {
    public LampTile(ResourceName name) {
        super(name);
        this.addProps(StaticTileProps.TORCH_FACING);
    }

    @Override
    public int getLight(IWorld world, int x, int y, TileLayer layer) {
        return 100;
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
        return this.getLampState(world, x, y) != null;
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractPlayerEntity placer) {
        return this.getLampState(world, x, y);
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        if (!world.isClient()) {
            TileState state = this.getLampState(world, x, y);

            if (state == null) {
                world.destroyTile(x, y, layer, null, this.forceDrop);
            } else if (state != world.getState(x, y)) {
                world.setState(x, y, state);
            }
        }
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return this.getLampState(world, x, y) != null;
    }

    private TileState getLampState(IWorld world, int x, int y) {
        int meta = this.getFacingMeta(world, x, y);
        if (meta >= 0) {
            return this.getDefState().prop(StaticTileProps.TORCH_FACING, meta);
        } else {
            return null;
        }
    }

    protected int getFacingMeta(IWorld world, int x, int y) {
        if (world.getState(x, y - 1).getTile().hasSolidSurface(world, x, y - 1, TileLayer.MAIN)) {
            return 0;
        } else if (world.getState(x + 1, y).getTile().isFullTile()) {
            return 1;
        } else if (world.getState(x - 1, y).getTile().isFullTile()) {
            return 2;
        } else if (world.getState(TileLayer.BACKGROUND, x, y).getTile().isFullTile()) {
            return 3;
        } else {
            return -1;
        }
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new LampTileRenderer(name);
    }
}
