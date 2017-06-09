package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
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
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getPlayer() != null){
                game.getPlayer().resetAndSpawn(game);
            }
            return true;
        });
    }
}
