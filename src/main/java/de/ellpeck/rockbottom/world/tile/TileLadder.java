package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.List;

public class TileLadder extends TileBasic {

    public TileLadder() {
        super(ResourceName.intern("ladder"));
    }

    @Override
    public boolean canClimb(IWorld world, int x, int y, TileLayer layer, TileState state, BoundBox entityBox, BoundBox entityBoxMotion, List<BoundBox> tileBoxes, Entity entity) {
        return Util.floor(entity.getY()) == y;
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
        if (layer == TileLayer.MAIN) {
            ItemInstance instance = player.getSelectedItem();
            if (instance.getItem() == GameContent.TILE_PLATFORM.getItem()) {
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
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player) {
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
