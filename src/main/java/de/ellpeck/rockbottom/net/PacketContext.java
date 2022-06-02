package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class PacketContext implements IPacketContext {

    public static final AttributeKey<AbstractPlayerEntity> PLAYER_ATTRIBUTE_KEY = AttributeKey.newInstance("player");

    private final ChannelHandlerContext context;
    private final Attribute<AbstractPlayerEntity> playerAttr;

    public PacketContext(ChannelHandlerContext context) {
        this.context = context;
        this.playerAttr = context.channel().hasAttr(PLAYER_ATTRIBUTE_KEY) ? context.channel().attr(PLAYER_ATTRIBUTE_KEY) : null;
    }

    @Override
    public ChannelHandlerContext getChannelContext() {
        return this.context;
    }

    @Override
    public AbstractPlayerEntity getSender() {
        if (this.playerAttr != null) {
            return this.playerAttr.get();
        }

        throw new UnsupportedOperationException("There is no player sender when receiving packets on the client");
    }
}
