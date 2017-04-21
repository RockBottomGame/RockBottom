package de.ellpeck.rockbottom.net.packet;

import de.ellpeck.rockbottom.RockBottom;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public interface IPacket{

    void toBuffer(ByteBuf buf) throws IOException;

    void fromBuffer(ByteBuf buf) throws IOException;

    void handle(RockBottom game, ChannelHandlerContext context);
}
