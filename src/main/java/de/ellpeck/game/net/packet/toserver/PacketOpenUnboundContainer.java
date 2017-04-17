package de.ellpeck.game.net.packet.toserver;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.container.ContainerInventory;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketOpenUnboundContainer implements IPacket{

    public static final int CLOSE_ID = -2;

    private UUID playerId;
    private int id;

    public PacketOpenUnboundContainer(UUID playerId, int id){
        this.playerId = playerId;
        this.id = id;
    }

    public PacketOpenUnboundContainer(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.id);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.id = buf.readInt();
    }

    @Override
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                EntityPlayer player = game.world.getPlayer(this.playerId);
                if(player != null){
                    if(this.id == CLOSE_ID){
                        player.closeContainer();
                    }
                    else if(this.id == 0){
                        player.openContainer(player.inventoryContainer);
                    }
                }
            }
        });
    }
}
