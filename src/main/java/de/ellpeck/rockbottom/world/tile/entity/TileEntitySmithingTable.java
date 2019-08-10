package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
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
	public ItemInstance getTool(Item tool) {
		return tool == GameContent.ITEM_HAMMER ? hammerSlot.get(0) : null;
	}
}
