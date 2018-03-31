package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketHealth implements IPacket{

    private int health;

    public PacketHealth(int health){
        this.health = health;
    }

    public PacketHealth(){
    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeInt(this.health);
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.health = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        if(game.getPlayer() != null){
            game.getPlayer().setHealth(this.health);
        }
    }
}
