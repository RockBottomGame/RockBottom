package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.data.set.DataSet;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.toclient.PacketTileEntityData;
import de.ellpeck.rockbottom.world.World;

public class TileEntity{

    public final World world;
    public final int x;
    public final int y;

    public TileEntity(World world, int x, int y){
        this.world = world;
        this.x = x;
        this.y = y;
    }

    public void update(RockBottom game){

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
