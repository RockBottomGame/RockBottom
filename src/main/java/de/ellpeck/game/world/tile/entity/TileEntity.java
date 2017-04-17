package de.ellpeck.game.world.tile.entity;

import de.ellpeck.game.Constants;
import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.NetUtil;
import de.ellpeck.game.net.packet.toclient.PacketTileEntityData;
import de.ellpeck.game.world.World;
import io.netty.buffer.ByteBuf;

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

    public void saveSynced(DataSet set){

    }

    public void loadSynced(DataSet set){

    }

    public void sendToClients(){
        if(NetHandler.isServer()){
            NetHandler.sendToAllPlayers(this.world, new PacketTileEntityData(this.x, this.y, this));
        }
    }

    public boolean doesSave(){
        return true;
    }
}
