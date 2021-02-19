package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.PotTileRenderer;
import de.ellpeck.rockbottom.world.tile.entity.PotTileEntity;

import java.util.List;

public class PotTile extends BasicTile {

    public PotTile() {
        super(ResourceName.intern("pot"));
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(manager.localize(ResourceName.intern("info.pot")));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        ItemInstance held = player.getSelectedItem();
        PotTileEntity pot = world.getTileEntity(x, y, PotTileEntity.class);
        if (held != null && pot != null) {
            if (pot.tryPlaceFlower(held)) {
                if (!player.getGameMode().isCreative()) {
                    ItemInstance leftover = held.removeAmount(1);
                    player.getInv().set(player.getSelectedSlot(), leftover.nullIfEmpty());
                }
                return true;
            }
        }

        return super.onInteractWith(world, x, y, layer, mouseX, mouseY, player);
    }

    @Override
    public boolean onInteractWithBreakKey(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        PotTileEntity pot = world.getTileEntity(x, y, PotTileEntity.class);
        if (pot != null) {
            if (pot.tryRemoveFlower(!player.getGameMode().isCreative())) {
                return true;
            }
        }

        return super.onInteractWith(world, x, y, layer, mouseX, mouseY, player);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
        return this.canBeHere(world, x, y, layer);
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return this.canBeHere(world, x, y, layer);
    }

    private boolean canBeHere(IWorld world, int x, int y, TileLayer layer) {
        if (world.isPosLoaded(x, y - 1)) {
            return world.getState(layer, x, y - 1).getTile().hasSolidSurface(world, x, y, layer);
        }

        return false;
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
    protected ITileRenderer createRenderer(ResourceName name) {
        return new PotTileRenderer(name);
    }

    @Override
    public boolean canProvideTileEntity() {
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y, TileLayer layer) {
        return new PotTileEntity(world, x, y, layer);
    }
}
