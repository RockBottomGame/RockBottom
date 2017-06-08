package de.ellpeck.rockbottom.game.world.tile.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.game.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.game.net.NetHandler;
import de.ellpeck.rockbottom.game.net.packet.toclient.PacketTileEntityData;
import de.ellpeck.rockbottom.game.util.Util;
import de.ellpeck.rockbottom.game.world.entity.EntityItem;
import io.netty.buffer.ByteBuf;

public class TileEntity{

    public final IWorld world;
    public final int x;
    public final int y;

    public TileEntity(IWorld world, int x, int y){
        this.world = world;
        this.x = x;
        this.y = y;
    }

    public void update(IGameInstance game){
        if(NetHandler.isServer()){
            if(this.world.getWorldInfo().totalTimeInWorld%this.getSyncInterval() == 0 && this.needsSync()){
                this.sendToClients();
                this.onSync();
            }
        }
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

    protected boolean needsSync(){
        return false;
    }

    protected void onSync(){

    }

    protected int getSyncInterval(){
        return 10;
    }

    protected void sendToClients(){
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
