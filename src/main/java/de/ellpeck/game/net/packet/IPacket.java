package de.ellpeck.game.net.packet;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface IPacket{

    void toBuffer(ByteBuf buf) throws IOException;

    void fromBuffer(ByteBuf buf) throws IOException;

    void handle();
}
