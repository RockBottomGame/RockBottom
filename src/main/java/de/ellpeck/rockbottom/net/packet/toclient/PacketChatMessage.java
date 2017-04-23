package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.net.NetUtil;
import de.ellpeck.rockbottom.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketChatMessage implements IPacket{

    private String message;

    public PacketChatMessage(String message){
        this.message = message;
    }

    public PacketChatMessage(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        NetUtil.writeStringToBuffer(this.message, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.message = NetUtil.readStringFromBuffer(buf);
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            game.chatLog.displayMessage(this.message);
            return true;
        });
    }
}
