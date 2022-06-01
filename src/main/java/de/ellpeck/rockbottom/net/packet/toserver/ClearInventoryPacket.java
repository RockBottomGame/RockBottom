package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class ClearInventoryPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("clear_inventory");

    private UUID id;

    public ClearInventoryPacket(UUID id) {
        this.id = id;
    }

    public ClearInventoryPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        NetUtil.writeUUIDToBuffer(buf, this.id);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.id = NetUtil.readUUIDFromBuffer(buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        AbstractPlayerEntity player = game.getWorld().getPlayer(this.id);
        if (player == null)
            return;

        player.getInv().clear();
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
