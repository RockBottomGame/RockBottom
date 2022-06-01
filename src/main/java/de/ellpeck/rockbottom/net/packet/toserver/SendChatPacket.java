package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class SendChatPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("send_chat");

    private UUID playerId;
    private String message;

    public SendChatPacket(UUID playerId, String message) {
        this.playerId = playerId;
        this.message = message;
    }

    public SendChatPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        NetUtil.writeStringToBuffer(buf, this.message);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.message = NetUtil.readStringFromBuffer(buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getWorld() != null) {
            AbstractPlayerEntity player = game.getWorld().getPlayer(this.playerId);
            if (player != null) {
                game.getChatLog().sendCommandSenderMessage(this.message, player);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
