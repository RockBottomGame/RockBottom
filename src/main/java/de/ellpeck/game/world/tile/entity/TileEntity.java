package de.ellpeck.game.world.tile.entity;

import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.world.World;

public class TileEntity{

    public final World world;
    public final int x;
    public final int y;

    public TileEntity(World world, int x, int y){
        this.world = world;
        this.x = x;
        this.y = y;
    }

    public void update(Game game){

    }

    public boolean shouldRemove(){
        return false;
    }

    public void save(DataSet set){

    }

    public void load(DataSet set){

    }

    public boolean isDirty(){
        return false;
    }
}
