package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketAttack implements IPacket{

    private UUID playerId;
    private double x;
    private double y;

    public PacketAttack(UUID playerId, double x, double y){
        this.playerId = playerId;
        this.x = x;
        this.y = y;
    }

    public PacketAttack(){

    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        if(game.getWorld() != null){
            AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
            if(player != null){
                InteractionManager.attackEntity(player, this.x, this.y);
            }
        }
    }
}
