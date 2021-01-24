package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.List;

public class LadderTile extends BasicTile {

    public LadderTile() {
        super(ResourceName.intern("ladder"));
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
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        if (layer == TileLayer.MAIN) {
            ItemInstance instance = player.getSelectedItem();
            if (instance != null && instance.getItem() == GameContent.TILE_PLATFORM.getItem()) {
                if (!world.isClient()) {
                    world.setState(x, y, GameContent.TILE_PLATFORM.getDefState().prop(StaticTileProps.HAS_LADDER, true));
                    instance.removeAmount(1);
                }
                return true;
            }
        }
        return super.onInteractWith(world, x, y, layer, mouseX, mouseY, player);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
        return this.isLadderPos(world, x, y);
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return this.isLadderPos(world, x, y);
    }

    private boolean isLadderPos(IWorld world, int x, int y) {
        Tile below = world.getState(x, y - 1).getTile();
        return below == this || below.isFullTile() || world.getState(TileLayer.BACKGROUND, x, y).getTile().isFullTile();
    }
}
