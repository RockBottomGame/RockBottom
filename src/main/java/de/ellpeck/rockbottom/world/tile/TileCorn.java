package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.render.tile.TileCornRenderer;

import java.util.Collections;
import java.util.List;

public class TileCorn extends TileBasic {

    public TileCorn() {
        super(ResourceName.intern("corn"));
        this.addProps(StaticTileProps.TOP_HALF, StaticTileProps.CORN_GROWTH);
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new TileCornRenderer(name);
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return world.getState(layer, x, y).get(StaticTileProps.TOP_HALF) || this.canBeHere(world, x, y, layer);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player) {
        return world.isPosLoaded(x, y - 1, false) && this.canBeHere(world, x, y, layer);
    }

    private boolean canBeHere(IWorld world, int x, int y, TileLayer layer) {
        return world.getState(layer, x, y - 1).getTile().canKeepFarmablePlants(world, x, y, layer) && world.getState(TileLayer.LIQUIDS, x, y).getTile().isAir();
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer) {
        if (Util.RANDOM.nextFloat() >= 0.95F) {
            TileState state = world.getState(layer, x, y);
            if (!state.get(StaticTileProps.TOP_HALF)) {
                int growth = state.get(StaticTileProps.CORN_GROWTH);
                if (growth < 9) {
                    if (growth >= 3) {
                        TileState above = world.getState(layer, x, y + 1);
                        if (above.getTile() == this) {
                            world.setState(layer, x, y + 1, above.prop(StaticTileProps.CORN_GROWTH, growth + 1));
                        } else if (world.getState(layer, x, y + 1).getTile().canReplace(world, x, y + 1, layer)) {
                            world.setState(layer, x, y + 1, this.getDefState().prop(StaticTileProps.TOP_HALF, true).prop(StaticTileProps.CORN_GROWTH, growth + 1));
                        } else {
                            return;
                        }
                    }
                    world.setState(layer, x, y, state.prop(StaticTileProps.CORN_GROWTH, growth + 1));
                }
            }
        }
    }

    @Override
    public boolean onInteractWithBreakKey(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
        TileState state = world.getState(layer, x, y);
        if (state.get(StaticTileProps.TOP_HALF) && state.get(StaticTileProps.CORN_GROWTH) >= 9) {
            this.onDestroyed(world, x, y, null, layer, true);

            if (!world.isClient()) {
                world.setState(layer, x, y, state.prop(StaticTileProps.CORN_GROWTH, 5));
                world.setState(layer, x, y - 1, world.getState(layer, x, y - 1).prop(StaticTileProps.CORN_GROWTH, 5));

                if (player != null && ConstructionRegistry.simpleHoe != null) {
                    player.getKnowledge().teachRecipe(ConstructionRegistry.simpleHoe);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
        TileState state = world.getState(layer, x, y);
        if (state.get(StaticTileProps.TOP_HALF) && state.get(StaticTileProps.CORN_GROWTH) >= 9) {
            return Collections.singletonList(new ItemInstance(this, Util.RANDOM.nextInt(3) + 1));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void doBreak(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer breaker, boolean isRightTool, boolean allowDrop) {
        if (!world.isClient()) {
            boolean drop = allowDrop && (this.forceDrop || isRightTool);
            boolean topHalf = world.getState(layer, x, y).get(StaticTileProps.TOP_HALF);
            if (topHalf || world.getState(layer, x, y + 1).getTile() == this) {
                world.destroyTile(x, y + (topHalf ? -1 : 1), layer, breaker, drop);
            }
            world.destroyTile(x, y, layer, breaker, drop);
        }
    }
}
