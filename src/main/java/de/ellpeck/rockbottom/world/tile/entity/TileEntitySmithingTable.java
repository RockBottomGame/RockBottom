package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.tile.entity.IToolStation;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.entity.TileInventory;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileEntitySmithingTable extends TileEntity implements IToolStation {

	private TileInventory hammerSlot = new TileInventory(this, (inst) -> inst != null && inst.getItem() == GameContent.ITEM_HAMMER);

	public TileEntitySmithingTable(IWorld world, int x, int y, TileLayer layer) {
		super(world, x, y, layer);
	}

	@Override
	public IFilteredInventory getTileInventory() {
		return this.hammerSlot;
	}

	@Override
	public int getToolSlot(Item tool) {
		return tool == GameContent.ITEM_HAMMER ? 0 : -1;
	}

	@Override
	public void save(DataSet set, boolean forSync) {
		if (!forSync) {
			hammerSlot.save(set);
		}
	}

	@Override
	public void load(DataSet set, boolean forSync) {
		if (!forSync) {
			hammerSlot.load(set);
		}
	}
}
