package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.GuiSign;
import de.ellpeck.rockbottom.render.tile.TileSignRenderer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySign;

public class TileSign extends TileBasic {

    public TileSign() {
        super(ResourceName.intern("sign"));
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
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player) {
        return world.getState(TileLayer.BACKGROUND, x, y).getTile().isFullTile();
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return this.canPlace(world, x, y, layer, null);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean canProvideTileEntity() {
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y, TileLayer layer) {
        return new TileEntitySign(world, x, y, layer);
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
        TileEntitySign tile = world.getTileEntity(layer, x, y, TileEntitySign.class);
        return tile != null && player.openGui(new GuiSign(tile));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new TileSignRenderer(name);
    }
}
