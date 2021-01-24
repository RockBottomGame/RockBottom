package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class AirTile extends Tile {

    public AirTile() {
        super(ResourceName.intern("air"));
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    public boolean canBreak(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player, boolean isRightTool) {
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
