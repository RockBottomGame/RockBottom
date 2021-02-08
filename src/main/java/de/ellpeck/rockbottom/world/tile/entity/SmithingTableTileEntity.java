package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolProperty;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.tile.entity.IToolStation;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.entity.TileInventory;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Collections;
import java.util.List;

public class SmithingTableTileEntity extends TileEntity implements IToolStation {

	private TileInventory hammerSlot = new TileInventory(this, (inst) -> inst != null && inst.getItem() == GameContent.Items.HAMMER);

	public SmithingTableTileEntity(IWorld world, int x, int y, TileLayer layer) {
		super(world, x, y, layer);
	}

	@Override
	public IFilteredInventory getTileInventory() {
		return this.hammerSlot;
	}

    @Override
    public List<ItemInstance> getTools() {
        return Collections.singletonList(hammerSlot.get(0));
    }

    @Override
	public ItemInstance getTool(ToolProperty tool) {
		return tool == ToolProperty.HAMMER ? this.hammerSlot.get(0) : null;
	}

    @Override
    public ItemInstance insertTool(ItemInstance tool) {
        if (tool.getItem().hasToolProperty(tool, ToolProperty.HAMMER)) {
            return this.hammerSlot.add(tool, false);
        }
        return tool;
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
