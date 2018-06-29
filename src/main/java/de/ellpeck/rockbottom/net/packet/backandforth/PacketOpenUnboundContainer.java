package de.ellpeck.rockbottom.net.packet.backandforth;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.net.chat.command.CommandItemList;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketOpenUnboundContainer implements IPacket {

    public static final int ITEM_LIST_ID = 1;
    public static final int INV_ID = 0;
    public static final int CLOSE_ID = -1;

    private UUID playerId;
    private int id;

    public PacketOpenUnboundContainer(UUID playerId, int id) {
        this.playerId = playerId;
        this.id = id;
    }

    public PacketOpenUnboundContainer() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.id);
        if (this.playerId != null) {
            buf.writeLong(this.playerId.getMostSignificantBits());
            buf.writeLong(this.playerId.getLeastSignificantBits());
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.id = buf.readInt();
        if (buf.isReadable()) {
            this.playerId = new UUID(buf.readLong(), buf.readLong());
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getWorld() != null) {
            if (this.id == ITEM_LIST_ID) {
                AbstractEntityPlayer player = game.getPlayer();
                if (player != null) {
                    CommandItemList.open(player);
                }
            } else {
                AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
                if (player != null) {
                    if (this.id == CLOSE_ID) {
                        player.closeContainer();
                    } else if (this.id == INV_ID) {
                        player.openContainer(player.getInvContainer());
                    }
                }
            }
        }
    }
}
