package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileEntitySign extends TileEntity {

    public static final int TEXT_AMOUNT = 5;
    public final String[] text = new String[TEXT_AMOUNT];

    public TileEntitySign(IWorld world, int x, int y, TileLayer layer) {
        super(world, x, y, layer);

        for (int i = 0; i < this.text.length; i++) {
            this.text[i] = "";
        }
    }

    @Override
    public void save(DataSet set, boolean forSync) {
        for (int i = 0; i < this.text.length; i++) {
            set.addString("text_" + i, this.text[i]);
        }
    }

    @Override
    public void load(DataSet set, boolean forSync) {
        for (int i = 0; i < this.text.length; i++) {
            this.text[i] = set.getString("text_" + i);
        }
    }

    @Override
    public boolean doesTick() {
        return false;
    }
}
