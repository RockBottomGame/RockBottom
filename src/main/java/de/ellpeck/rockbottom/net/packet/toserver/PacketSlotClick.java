package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.init.RockBottomServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketSlotClick implements IPacket {

    public UUID playerUUID;
    public int slotId;
    public int button;

    public PacketSlotClick(AbstractEntityPlayer player, int slotId, int button) {
        this.playerUUID = player.getUniqueId();
        this.slotId = slotId;
        this.button = button;
    }

    public PacketSlotClick() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        NetUtil.writeUUIDToBuffer(buf, playerUUID);
        buf.writeInt(this.slotId);
        buf.writeInt(this.button);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerUUID = NetUtil.readUUIDFromBuffer(buf);
        this.slotId = buf.readInt();
        this.button = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        RockBottomServer server = (RockBottomServer) game;
        AbstractEntityPlayer player = server.getWorld().getPlayer(this.playerUUID);
        ItemContainer container = player.getContainer();
        ContainerSlot slot = container.getSlot(this.slotId);
        ItemInstance instance = slot.get();
        if (instance != null)
            instance.getItem().onClickInSlot(player, container, slot, instance, this.button, container.holdingInst);
    }
}