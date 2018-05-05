package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketActiveItem implements IPacket{

    private UUID playerId;
    private int slot;
    private ItemInstance newSlotItem;

    public PacketActiveItem(UUID playerId, int slot, ItemInstance newSlotItem){
        this.playerId = playerId;
        this.slot = slot;
        this.newSlotItem = newSlotItem;
    }

    public PacketActiveItem(){
    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.slot);

        DataSet set = new DataSet();
        if(this.newSlotItem != null){
            this.newSlotItem.save(set);
        }
        NetUtil.writeSetToBuffer(set, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.slot = buf.readInt();

        DataSet set = new DataSet();
        NetUtil.readSetFromBuffer(set, buf);
        if(!set.isEmpty()){
            this.newSlotItem = ItemInstance.load(set);
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        IWorld world = game.getWorld();
        if(world != null){
            Entity entity = world.getEntity(this.playerId);
            if(entity instanceof EntityPlayer){
                EntityPlayer player = (EntityPlayer)entity;
                player.setSelectedSlot(this.slot);
                player.getInv().set(this.slot, this.newSlotItem);
            }
        }
    }
}
