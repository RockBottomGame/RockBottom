package de.ellpeck.game.net.packet.toserver;

import de.ellpeck.game.Game;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketPlayerMovement implements IPacket{

    private UUID playerId;
    private int type;

    public PacketPlayerMovement(UUID playerId, int type){
        this.playerId = playerId;
        this.type = type;
    }

    public PacketPlayerMovement(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.type);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.type = buf.readInt();
    }

    @Override
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                EntityPlayer player = game.world.getPlayer(this.playerId);
                if(player != null){
                    player.move(this.type);
                }
            }
        });
    }
}
