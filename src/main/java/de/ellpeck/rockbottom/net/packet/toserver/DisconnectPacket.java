package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.net.server.ConnectedPlayer;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class DisconnectPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("disconnect");

    @Override
    public void toBuffer(ByteBuf buf) {}

    @Override
    public void fromBuffer(ByteBuf buf) {}

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        ConnectedPlayer.disconnectPlayer(player);
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
