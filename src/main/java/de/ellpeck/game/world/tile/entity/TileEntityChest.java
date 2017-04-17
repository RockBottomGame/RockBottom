package de.ellpeck.game.world.tile.entity;

import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.inventory.Inventory;
import de.ellpeck.game.world.World;

public class TileEntityChest extends TileEntity{

    public int openCount;
    public final Inventory inventory = new Inventory(20);

    public TileEntityChest(World world, int x, int y){
        super(world, x, y);
        this.inventory.addChangeCallback((inv, slot, newInstance) -> this.world.setDirty(this.x, this.y));
    }

    @Override
    public void save(DataSet set){
        super.save(set);
        this.inventory.save(set);
    }

    @Override
    public void load(DataSet set){
        super.load(set);
        this.inventory.load(set);
    }
}
