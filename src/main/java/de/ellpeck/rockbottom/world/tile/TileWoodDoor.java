package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.EntityLiving;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.TileLiquid;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.TileDoorRenderer;

import java.util.Collections;
import java.util.List;

public class TileWoodDoor extends TileBasic {

    private final BoundBox boxLeft = new BoundBox(0D, 0D, 2D / 12D, 1D);
    private final BoundBox boxRight = new BoundBox(1D - 2D / 12D, 0D, 1D, 1D);

    public TileWoodDoor(ResourceName name) {
        super(name);
        this.addProps(StaticTileProps.TOP_HALF, StaticTileProps.OPEN, StaticTileProps.FACING_RIGHT);
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new TileDoorRenderer(name);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player) {
        return world.getState(x, y - 1).getTile().isFullTile() && world.getState(x, y + 1).getTile().canReplace(world, x, y + 1, layer);
    }

	@Override
	public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer) {
		return getDefState().prop(StaticTileProps.FACING_RIGHT, placer.facing == Direction.RIGHT);
	}

	@Override
    public void doPlace(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractEntityPlayer placer) {
        if (!world.isClient()) {
            TileState state = getPlacementState(world, x, y, layer, instance, placer);
            world.setState(layer, x, y, state.prop(StaticTileProps.TOP_HALF, false));
            world.setState(layer, x, y + 1, state.prop(StaticTileProps.TOP_HALF, true));
        }
    }

	@Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
        TileState state = world.getState(layer, x, y);
        int yAdd = state.get(StaticTileProps.TOP_HALF) ? -1 : 1;

        if (world.getEntities(new BoundBox(0, 0, 1, 2).add(x, y + yAdd), EntityLiving.class).isEmpty()) {
            if (!world.isClient()) {
                boolean open = !state.get(StaticTileProps.OPEN);

                world.setState(layer, x, y, state.prop(StaticTileProps.OPEN, open));
                world.setState(layer, x, y + yAdd, world.getState(layer, x, y + yAdd).prop(StaticTileProps.OPEN, open));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void doBreak(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer breaker, boolean isRightTool, boolean allowDrop) {
        if (!world.isClient()) {
            boolean drop = allowDrop && (this.forceDrop || isRightTool);
            TileState state = world.getState(layer, x, y);

            world.destroyTile(x, y, layer, breaker, drop);
            world.destroyTile(x, y + (state.get(StaticTileProps.TOP_HALF) ? -1 : 1), layer, breaker, drop);
        }
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
        return world.getState(layer, x, y).get(StaticTileProps.TOP_HALF) ? Collections.emptyList() : super.getDrops(world, x, y, layer, destroyer);
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public boolean canLiquidSpread(IWorld world, int x, int y, TileLiquid liquid, Direction dir) {
        TileState state = world.getState(x, y);
        boolean facingRight = state.get(StaticTileProps.FACING_RIGHT);
        return state.get(StaticTileProps.OPEN) || (facingRight && dir == Direction.LEFT) || (!facingRight && dir == Direction.RIGHT);
    }

    @Override
    public BoundBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        if (state.get(StaticTileProps.OPEN)) {
            return null;
        } else {
            return state.get(StaticTileProps.FACING_RIGHT) ? this.boxRight : this.boxLeft;
        }
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return world.getState(layer, x, y).get(StaticTileProps.TOP_HALF) || world.getState(layer, x, y - 1).getTile().isFullTile();
    }
}
