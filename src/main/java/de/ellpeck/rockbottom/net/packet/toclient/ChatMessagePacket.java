package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ChatMessagePacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("chat_message");

    private ChatComponent message;

    public ChatMessagePacket(ChatComponent message) {
        this.message = message;
    }

    public ChatMessagePacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        DataSet set = new DataSet();
        this.message.save(set);
        NetUtil.writeSetToBuffer(set, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        DataSet set = new DataSet();
        NetUtil.readSetFromBuffer(set, buf);
        this.message = ChatComponent.createFromSet(set);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        game.getChatLog().displayMessage(this.message);
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
