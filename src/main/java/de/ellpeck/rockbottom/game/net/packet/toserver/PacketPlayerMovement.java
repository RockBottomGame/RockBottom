package de.ellpeck.rockbottom.game.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketPlayerMovement implements IPacket{

    private UUID playerId;
    private int type;

    public PacketPlayerMovement(UUID playerId, int type){
        this.playerId = playerId;
        this.type = type;
    }

    public PacketPlayerMovement(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.type);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.type = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getWorld() != null){
                AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
                if(player != null){
                    player.move(this.type);
                }
            }
            return true;
        });
    }
}
