package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.render.tile.MultiTileRenderer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.ConstructionTableGui;
import de.ellpeck.rockbottom.gui.container.ConstructionTableContainer;
import de.ellpeck.rockbottom.world.tile.entity.ConstructionTableTileEntity;

import java.util.List;

public class ConstructionTableTile extends MultiTile {

    public ConstructionTableTile() {
        super(ResourceName.intern("construction_table"));
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(manager.localize(ResourceName.intern("info.construction_table")));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        Pos2 main = this.getMainPos(x, y, world.getState(layer, x, y));
        ConstructionTableTileEntity tile = world.getTileEntity(layer, main.getX(), main.getY(), ConstructionTableTileEntity.class);
        return tile != null && player.openGuiContainer(new ConstructionTableGui(player, tile), new ConstructionTableContainer(player, tile));
    }

    @Override
    public boolean isPlatform() {
        return true;
    }

    @Override
    public boolean hasSolidSurface(IWorld world, int x, int y, TileLayer layer) {
        return true;
    }

    @Override
    public boolean canProvideTileEntity() {
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y, TileLayer layer) {
        return this.isMainPos(x, y, world.getState(layer, x, y)) ? new ConstructionTableTileEntity(world, x, y, layer) : null;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
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
    protected ITileRenderer createRenderer(ResourceName name) {
        return new MultiTileRenderer<>(name, this);
    }

    @Override
    protected boolean[][] makeStructure() {
        return new boolean[][]{
                {true, true}
        };
    }

    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @Override
    public int getMainX() {
        return 0;
    }

    @Override
    public int getMainY() {
        return 0;
    }
}
