package de.ellpeck.game.net.packet;

import de.ellpeck.game.Game;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public interface IPacket{

    void toBuffer(ByteBuf buf) throws IOException;

    void fromBuffer(ByteBuf buf) throws IOException;

    void handle(Game game, ChannelHandlerContext context);
}
