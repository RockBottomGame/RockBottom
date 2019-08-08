package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileAir extends Tile {

    public TileAir() {
        super(ResourceName.intern("air"));
    }

    @Override
    public BoundBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    public boolean canBreak(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player, boolean isRightTool) {
        return false;
    }

    @Override
    protected boolean hasItem() {
        return false;
    }

    @Override
    public boolean canReplace(IWorld world, int x, int y, TileLayer layer) {
        return true;
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public boolean isAir() {
        return true;
    }
}
