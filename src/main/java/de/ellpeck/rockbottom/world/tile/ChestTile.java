package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.ChestGui;
import de.ellpeck.rockbottom.gui.container.ChestContainer;
import de.ellpeck.rockbottom.render.tile.ChestTileRenderer;
import de.ellpeck.rockbottom.world.tile.entity.ChestTileEntity;

import java.util.List;

public class ChestTile extends BasicTile {

    public ChestTile() {
        super(ResourceName.intern("chest"));
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
    public boolean canProvideTileEntity() {
        return true;
    }

    @Override
    public List<BoundingBox> getPlatformBounds(IWorld world, int x, int y, TileLayer layer, TileState state, MovableWorldObject object, BoundingBox objectBox, BoundingBox objectBoxMotion) {
        return RockBottomAPI.getApiHandler().getDefaultPlatformBounds(world, x, y, layer, 10/12d, 8/12f, state, object, objectBox);
    }

    @Override
    public boolean isPlatform() {
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y, TileLayer layer) {
        return new ChestTileEntity(world, x, y, layer);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        ChestTileEntity tile = world.getTileEntity(x, y, ChestTileEntity.class);
        return tile != null && player.openGuiContainer(new ChestGui(player, tile.getTileInventory()), new ChestContainer(player, tile));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new ChestTileRenderer(name);
    }
}
