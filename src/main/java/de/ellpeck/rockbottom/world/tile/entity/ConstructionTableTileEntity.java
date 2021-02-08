package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.CombinedInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolProperty;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.tile.entity.IToolStation;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.entity.TileInventory;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.ArrayList;
import java.util.List;

public class ConstructionTableTileEntity extends TileEntity implements IToolStation {

    private final TileInventory chiselSlot = new TileInventory(this, inst -> inst != null && inst.getItem().hasToolProperty(inst, ToolProperty.CHISEL));
    private final TileInventory hammerSlot = new TileInventory(this, inst -> inst != null && inst.getItem().hasToolProperty(inst, ToolProperty.HAMMER));
    private final TileInventory sawSlot = new TileInventory(this, inst -> inst != null && inst.getItem().hasToolProperty(inst, ToolProperty.SAW));
    private final TileInventory malletSlot = new TileInventory(this, inst -> inst != null && inst.getItem().hasToolProperty(inst, ToolProperty.MALLET));
    private final TileInventory wrenchSlot = new TileInventory(this, inst -> inst != null && inst.getItem().hasToolProperty(inst, ToolProperty.WRENCH));
    private final CombinedInventory inventory = new CombinedInventory(this.chiselSlot, this.hammerSlot, this.sawSlot, this.malletSlot, this.wrenchSlot);

    public ConstructionTableTileEntity(IWorld world, int x, int y, TileLayer layer) {
        super(world, x, y, layer);
    }

    @Override
    public IFilteredInventory getTileInventory() {
        return this.inventory;
    }

    @Override
    public void update(IGameInstance game) {
        super.update(game);
    }

    @Override
    public List<ItemInstance> getTools() {
        List<ItemInstance> items = new ArrayList<>();
        for (ItemInstance item : this.inventory) {
            items.add(item);
        }
        return items;
    }

    @Override
    public ItemInstance getTool(ToolProperty tool) {
        if (tool == ToolProperty.CHISEL) {
            return this.chiselSlot.get(0);
        } else if (tool == ToolProperty.HAMMER) {
            return this.hammerSlot.get(0);
        } else if (tool == ToolProperty.SAW) {
            return this.sawSlot.get(0);
        } else if (tool == ToolProperty.MALLET) {
            return this.malletSlot.get(0);
        } else if (tool == ToolProperty.WRENCH) {
            return this.wrenchSlot.get(0);
        }
        return null;
    }

    @Override
    public ItemInstance insertTool(ItemInstance tool) {
        if (tool.getItem().hasToolProperty(tool, ToolProperty.CHISEL)) {
            return this.chiselSlot.add(tool, false);
        } else if (tool.getItem().hasToolProperty(tool, ToolProperty.HAMMER)) {
            return this.hammerSlot.add(tool, false);
        } else if (tool.getItem().hasToolProperty(tool, ToolProperty.SAW)) {
            return this.sawSlot.add(tool, false);
        } else if (tool.getItem().hasToolProperty(tool, ToolProperty.MALLET)) {
            return this.malletSlot.add(tool, false);
        } else if (tool.getItem().hasToolProperty(tool, ToolProperty.WRENCH)) {
            return this.wrenchSlot.add(tool, false);
        }
        return tool;
    }

    @Override
    public void save(DataSet set, boolean forSync) {
        if (!forSync) {
            this.inventory.save(set);
        }
    }

    @Override
    public void load(DataSet set, boolean forSync) {
        if (!forSync) {
            this.inventory.load(set);
        }
    }
}
