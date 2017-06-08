package de.ellpeck.rockbottom.game.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketOpenUnboundContainer implements IPacket{

    public static final int CLOSE_ID = -2;

    private UUID playerId;
    private int id;

    public PacketOpenUnboundContainer(UUID playerId, int id){
        this.playerId = playerId;
        this.id = id;
    }

    public PacketOpenUnboundContainer(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.id);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.id = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getWorld() != null){
                AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
                if(player != null){
                    if(this.id == CLOSE_ID){
                        player.closeContainer();
                    }
                    else if(this.id == 0){
                        player.openContainer(player.getInvContainer());
                    }
                }
            }
            return true;
        });
    }
}
