package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.net.server.ConnectedPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketDisconnect implements IPacket {

    private UUID id;

    public PacketDisconnect(UUID id) {
        this.id = id;
    }

    public PacketDisconnect() {

    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.id.getMostSignificantBits());
        buf.writeLong(this.id.getLeastSignificantBits());
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.id = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        AbstractEntityPlayer player = game.getWorld().getPlayer(this.id);
        ConnectedPlayer.disconnectPlayer(player);
    }
}
