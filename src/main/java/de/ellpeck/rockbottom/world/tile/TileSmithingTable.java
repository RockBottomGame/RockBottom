package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.GuiSmithingTable;
import de.ellpeck.rockbottom.gui.container.ContainerSmithingTable;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySmithingTable;

public class TileSmithingTable extends MultiTile {

	public TileSmithingTable() {
		super(ResourceName.intern("smithing_table"));
	}

	@Override
	public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player) {
		Pos2 main = this.getMainPos(x, y, world.getState(layer, x, y));
		TileEntitySmithingTable tile = world.getTileEntity(layer, main.getX(), main.getY(), TileEntitySmithingTable.class);
		return tile != null && player.openGuiContainer(new GuiSmithingTable(player, tile), new ContainerSmithingTable(player, tile));
	}

	@Override
	public BoundBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
		return null;
	}

    @Override
    public boolean isPlatform() {
        return true;
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
	public TileEntity provideTileEntity(IWorld world, int x, int y, TileLayer layer) {
		return this.isMainPos(x, y, world.getState(layer, x, y)) ? new TileEntitySmithingTable(world, x, y, layer) : null;
	}

	@Override
	public boolean canProvideTileEntity() {
		return true;
	}

	@Override
	protected boolean[][] makeStructure() {
		return new boolean[][] { {true, true} };
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
