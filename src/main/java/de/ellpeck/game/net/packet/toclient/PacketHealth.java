package de.ellpeck.game.net.packet.toclient;

import de.ellpeck.game.Game;
import de.ellpeck.game.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketHealth implements IPacket{

    private int health;

    public PacketHealth(int health){
        this.health = health;
    }

    public PacketHealth(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.health);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.health = buf.readInt();
    }

    @Override
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.player != null){
                game.player.setHealth(this.health);
            }
            return true;
        });
    }
}
