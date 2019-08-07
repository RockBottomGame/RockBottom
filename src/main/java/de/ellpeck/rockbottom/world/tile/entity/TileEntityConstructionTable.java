package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.CombinedInventory;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.entity.TileInventory;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.item.ItemConstructionTool;

public class TileEntityConstructionTable extends TileEntity {

    private final TileInventory chiselSlot = new TileInventory(this, inst -> inst != null && inst.getItem() == GameContent.ITEM_CHISEL);
    private final TileInventory hammerSlot = new TileInventory(this, inst -> inst != null && inst.getItem() == GameContent.ITEM_HAMMER);
    private final TileInventory sawSlot = new TileInventory(this, inst -> inst != null && inst.getItem() == GameContent.ITEM_SAW);
    private final TileInventory malletSlot = new TileInventory(this, inst -> inst != null && inst.getItem() == GameContent.ITEM_MALLET);
    private final TileInventory wrenchSlot = new TileInventory(this, inst -> inst != null && inst.getItem() == GameContent.ITEM_WRENCH);
    private final CombinedInventory inventory = new CombinedInventory(this.chiselSlot, this.hammerSlot, this.sawSlot, this.malletSlot, this.wrenchSlot);

    public TileEntityConstructionTable(IWorld world, int x, int y, TileLayer layer) {
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

    public boolean containsTool(Item tool) {
        if (tool == GameContent.ITEM_CHISEL) {
            return chiselSlot.get(0) != null;
        } else if (tool == GameContent.ITEM_HAMMER) {
            return hammerSlot.get(0) != null;
        } else if (tool == GameContent.ITEM_SAW) {
            return sawSlot.get(0) != null;
        } else if (tool == GameContent.ITEM_MALLET) {
            return malletSlot.get(0) != null;
        } else if (tool == GameContent.ITEM_WRENCH) {
            return wrenchSlot.get(0) != null;
        }
        return tool == null;
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
