package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.net.server.ConnectedPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.newdawn.slick.util.Log;

import java.io.IOException;
import java.util.UUID;

public class PacketDisconnect implements IPacket{

    private UUID id;

    public PacketDisconnect(UUID id){
        this.id = id;
    }

    public PacketDisconnect(){

    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.id.getMostSignificantBits());
        buf.writeLong(this.id.getLeastSignificantBits());
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.id = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            AbstractEntityPlayer player = game.getWorld().getPlayer(this.id);
            ConnectedPlayer.disconnectPlayer(game, player);
            return true;
        });
    }
}
