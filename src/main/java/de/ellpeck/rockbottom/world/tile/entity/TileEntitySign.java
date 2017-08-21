package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.world.IWorld;

public class TileEntitySign extends TileEntity{

    public String text;

    public TileEntitySign(IWorld world, int x, int y){
        super(world, x, y);
    }

    @Override
    public void save(DataSet set, boolean forSync){
        super.save(set, forSync);
        if(this.text != null){
            set.addString("text", this.text);
        }
    }

    @Override
    public void load(DataSet set, boolean forSync){
        super.load(set, forSync);
        this.text = set.getString("text");
    }
}
