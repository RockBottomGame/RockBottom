package de.ellpeck.rockbottom.game.net.packet;

import de.ellpeck.rockbottom.game.RockBottom;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public interface IPacket{

    void toBuffer(ByteBuf buf) throws IOException;

    void fromBuffer(ByteBuf buf) throws IOException;

    void handle(RockBottom game, ChannelHandlerContext context);
}
