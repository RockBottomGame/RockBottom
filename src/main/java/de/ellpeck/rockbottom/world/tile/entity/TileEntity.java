package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.data.set.DataSet;
import de.ellpeck.rockbottom.inventory.IInventory;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.toclient.PacketTileEntityData;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.EntityItem;
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

    public void update(RockBottom game){

    }

    public boolean shouldRemove(){
        return false;
    }

    public void save(DataSet set){

    }

    public void load(DataSet set){

    }

    public void toBuf(ByteBuf buf){

    }

    public void fromBuf(ByteBuf buf){

    }

    public void sendToClients(){
        if(NetHandler.isServer()){
            NetHandler.sendToAllPlayers(this.world, new PacketTileEntityData(this.x, this.y, this));
        }
    }

    public boolean doesSave(){
        return true;
    }

    public void dropInventory(IInventory inventory){
        for(int i = 0; i < inventory.getSlotAmount(); i++){
            ItemInstance inst = inventory.get(i);
            if(inst != null){
                EntityItem.spawn(this.world, inst, this.x+0.5, this.y+0.5, Util.RANDOM.nextGaussian()*0.1, Util.RANDOM.nextGaussian()*0.1);
            }
        }
    }
}
