package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketChatMessage implements IPacket{

    private ChatComponent message;

    public PacketChatMessage(ChatComponent message){
        this.message = message;
    }

    public PacketChatMessage(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        DataSet set = new DataSet();
        this.message.save(set);
        NetUtil.writeSetToBuffer(set, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        DataSet set = new DataSet();
        NetUtil.readSetFromBuffer(set, buf);
        this.message = ChatComponent.createFromSet(set);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.getChatLog().displayMessage(this.message);
    }
}
