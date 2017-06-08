package de.ellpeck.rockbottom.game.net.packet.toserver;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.data.set.DataSet;
import de.ellpeck.rockbottom.game.gui.container.ItemContainer;
import de.ellpeck.rockbottom.game.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.game.item.ItemInstance;
import de.ellpeck.rockbottom.game.net.NetUtil;
import de.ellpeck.rockbottom.game.net.packet.IPacket;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketSlotModification implements IPacket{

    private final DataSet instSet = new DataSet();
    private UUID playerId;
    private int slot;

    public PacketSlotModification(UUID playerId, int slot, ItemInstance inst){
        this.playerId = playerId;
        this.slot = slot;

        if(inst != null){
            inst.save(this.instSet);
        }
    }

    public PacketSlotModification(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.slot);
        NetUtil.writeSetToBuffer(this.instSet, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.slot = buf.readInt();
        NetUtil.readSetFromBuffer(this.instSet, buf);
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                EntityPlayer player = game.world.getPlayer(this.playerId);
                if(player != null){
                    ItemContainer container = player.getContainer();
                    if(container != null){
                        ContainerSlot slot = container.getSlot(this.slot);

                        ItemInstance inst = null;
                        if(!this.instSet.isEmpty()){
                            inst = ItemInstance.load(this.instSet);
                        }
                        slot.set(inst);
                    }
                }
            }
            return true;
        });
    }
}
