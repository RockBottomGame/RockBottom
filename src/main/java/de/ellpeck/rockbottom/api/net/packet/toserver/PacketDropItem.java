package de.ellpeck.rockbottom.api.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketDropItem implements IPacket{

    private UUID playerId;
    private ItemInstance instance;

    public PacketDropItem(UUID playerId, ItemInstance instance){
        this.playerId = playerId;
        this.instance = instance;
    }

    public PacketDropItem(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());

        DataSet set = new DataSet();
        this.instance.save(set);
        NetUtil.writeSetToBuffer(set, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());

        DataSet set = new DataSet();
        NetUtil.readSetFromBuffer(set, buf);
        this.instance = ItemInstance.load(set);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getWorld() != null){
                AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
                if(player != null){
                    EntityItem.spawn(player.world, this.instance, player.x, player.y+1, player.facing.x*0.25, 0);
                }
            }
            return true;
        });
    }
}
