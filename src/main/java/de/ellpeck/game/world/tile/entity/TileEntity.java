package de.ellpeck.game.world.tile.entity;

import de.ellpeck.game.Game;
import org.newdawn.slick.GameContainer;

public class TileEntity{

    public final int x;
    public final int y;

    public TileEntity(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void update(Game game){

    }

    public boolean shouldRemove(){
        return false;
    }

}
