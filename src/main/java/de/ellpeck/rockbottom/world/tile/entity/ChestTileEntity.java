package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.entity.TileInventory;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.net.packet.toclient.ChestOpenPacket;

public class ChestTileEntity extends TileEntity {

    private final TileInventory inventory = new TileInventory(this, 20, Util.makeIntList(0, 20));
    private int openCount;

    public ChestTileEntity(IWorld world, int x, int y, TileLayer layer) {
        super(world, x, y, layer);
    }

    @Override
    public IFilteredInventory getTileInventory() {
        return this.inventory;
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

    public int getOpenCount() {
        return this.openCount;
    }

    public void setOpenCount(int count) {
        this.openCount = count;

        if (this.world.isServer()) {
            RockBottomAPI.getNet().sendToAllPlayersWithLoadedPos(this.world, new ChestOpenPacket(this.x, this.y, this.openCount > 0), this.x, this.y);
        }
    }
}
