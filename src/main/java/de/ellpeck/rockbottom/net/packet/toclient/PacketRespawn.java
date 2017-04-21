package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketRespawn implements IPacket{

    public PacketRespawn(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{

    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{

    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.player != null){
                game.player.resetAndSpawn(game);
            }
            return true;
        });
    }
}
