package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.data.set.DataSet;
import de.ellpeck.rockbottom.gui.container.ItemContainer;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetUtil;
import de.ellpeck.rockbottom.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketContainerData implements IPacket{

    private ItemInstance[] data;

    public PacketContainerData(ItemContainer container){
        this.data = new ItemInstance[container.slots.size()];
        for(int i = 0; i < this.data.length; i++){
            ItemInstance inst = container.slots.get(i).get();
            if(inst != null){
                this.data[i] = inst;
            }
        }
    }

    public PacketContainerData(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.data.length);
        for(ItemInstance inst : this.data){
            DataSet set = new DataSet();

            if(inst != null){
                inst.save(set);
            }

            NetUtil.writeSetToBuffer(set, buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        int amount = buf.readInt();
        this.data = new ItemInstance[amount];

        for(int i = 0; i < this.data.length; i++){
            DataSet set = new DataSet();
            NetUtil.readSetFromBuffer(set, buf);

            if(!set.isEmpty()){
                this.data[i] = ItemInstance.load(set);
            }
        }
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.player != null){
                ItemContainer container = game.player.getContainer();
                if(container != null && container.slots.size() == this.data.length){
                    for(int i = 0; i < this.data.length; i++){
                        container.slots.get(i).set(this.data[i]);
                    }
                }
            }
            return true;
        });
    }
}
