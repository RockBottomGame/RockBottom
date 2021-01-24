package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.TileItem;
import de.ellpeck.rockbottom.api.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Collections;
import java.util.List;

public class PebblesTile extends BasicTile {

    public PebblesTile() {
        super(ResourceName.intern("pebbles"));
    }

    @Override
    protected TileItem createItemTile() {
        return new TileItem(this.getName()) {
            @Override
            protected IItemRenderer createRenderer(ResourceName name) {
                return new DefaultItemRenderer(name);
            }

            @Override
            public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player, ItemInstance instance) {
                if (instance.getAmount() >= 3) {
                    if (super.onInteractWith(world, x, y, layer, mouseX, mouseY, player, instance)) {
                        player.getInv().remove(player.getSelectedSlot(), 2);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return world.getState(layer, x, y - 1).getTile().hasSolidSurface(world, x, y - 1, layer);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
        return world.isPosLoaded(x, y - 1, false) && world.getState(layer, x, y - 1).getTile().hasSolidSurface(world, x, y - 1, layer);
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
        return Collections.singletonList(new ItemInstance(this, Util.RANDOM.nextInt(3) + 1));
    }
}
