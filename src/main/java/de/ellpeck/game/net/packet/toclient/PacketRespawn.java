package de.ellpeck.game.net.packet.toclient;

import de.ellpeck.game.Game;
import de.ellpeck.game.net.packet.IPacket;
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
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.player != null){
                game.player.resetAndSpawn(game);
            }
            return true;
        });
    }
}
