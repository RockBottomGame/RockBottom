package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class SendChatPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("send_chat");

    private String message;

    public SendChatPacket(String message) {
        this.message = message;
    }

    public SendChatPacket() {}

    @Override
    public void toBuffer(ByteBuf buf) {
        NetUtil.writeStringToBuffer(buf, this.message);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.message = NetUtil.readStringFromBuffer(buf);
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        if (player != null) {
            game.getChatLog().sendCommandSenderMessage(this.message, player);
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
