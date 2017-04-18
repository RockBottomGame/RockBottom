package de.ellpeck.game.net.packet.toclient;

import de.ellpeck.game.Game;
import de.ellpeck.game.net.NetUtil;
import de.ellpeck.game.net.packet.IPacket;
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
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            game.chatLog.addMessage(this.message);
            return true;
        });
    }
}
