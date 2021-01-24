package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.LivingEntity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.LiquidTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.DoorTileRenderer;

import java.util.Collections;
import java.util.List;

public class WoodDoorTile extends BasicTile {

    private final BoundingBox boxLeft = new BoundingBox(0D, 0D, 2D / 12D, 1D);
    private final BoundingBox boxRight = new BoundingBox(1D - 2D / 12D, 0D, 1D, 1D);

    public WoodDoorTile(ResourceName name) {
        super(name);
        this.addProps(StaticTileProps.TOP_HALF, StaticTileProps.OPEN, StaticTileProps.FACING_RIGHT);
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new DoorTileRenderer(name);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
        return world.getState(x, y - 1).getTile().isFullTile() && world.getState(x, y + 1).getTile().canReplace(world, x, y + 1, layer);
    }

	@Override
	public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractPlayerEntity placer) {
		return getDefState().prop(StaticTileProps.FACING_RIGHT, placer.facing == Direction.RIGHT);
	}

	@Override
    public void doPlace(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractPlayerEntity placer) {
        if (!world.isClient()) {
            TileState state = getPlacementState(world, x, y, layer, instance, placer);
            world.setState(layer, x, y, state.prop(StaticTileProps.TOP_HALF, false));
            world.setState(layer, x, y + 1, state.prop(StaticTileProps.TOP_HALF, true));
        }
    }

	@Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        TileState state = world.getState(layer, x, y);
        int yAdd = state.get(StaticTileProps.TOP_HALF) ? -1 : 1;

        if (world.getEntities(new BoundingBox(0, 0, 1, 2).add(x, y + yAdd), LivingEntity.class).isEmpty()) {
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
    public void doBreak(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity breaker, boolean isRightTool, boolean allowDrop) {
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
    public boolean canLiquidSpread(IWorld world, int x, int y, LiquidTile liquid, Direction dir) {
        TileState state = world.getState(x, y);
        boolean facingRight = state.get(StaticTileProps.FACING_RIGHT);
        return state.get(StaticTileProps.OPEN) || (facingRight && dir == Direction.LEFT) || (!facingRight && dir == Direction.RIGHT);
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
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
