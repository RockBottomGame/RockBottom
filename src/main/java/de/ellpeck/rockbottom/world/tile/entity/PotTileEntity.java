package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.AbstractItemEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.IPotPlantable;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class PotTileEntity extends TileEntity {

    private ItemInstance flower;

    public PotTileEntity(IWorld world, int x, int y, TileLayer layer) {
        super(world, x, y, layer);
    }

    public boolean tryRemoveFlower(boolean doSpawn) {
        if (this.flower != null) {
            if (doSpawn) {
                AbstractItemEntity.spawn(this.world, this.flower, this.x + 0.5d, this.y + 0.5d, 0, 0);
            }
            this.flower = null;
            this.sendToClients();
            return true;
        }
        return false;
    }

    public boolean tryPlaceFlower(ItemInstance flower) {
        if (this.flower != null) {
            return false;
        }

        if (IPotPlantable.isPotPlantable(flower)) {
            this.flower = flower.copy().setAmount(1);
            this.sendToClients();
            return true;
        }
        return false;
    }

    @Override
    public void save(DataSet set, boolean forSync) {
        if (flower != null) {
            DataSet flowerData = new DataSet();
            this.flower.save(flowerData);
            set.addDataSet("flower", flowerData);
        }
    }

    @Override
    public void load(DataSet set, boolean forSync) {
        if (set.hasKey("flower")) {
            this.flower = ItemInstance.load(set.getDataSet("flower"));
        }
    }

    public ItemInstance getFlower() {
        return this.flower;
    }
}
