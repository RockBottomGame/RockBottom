package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketRespawn implements IPacket{

    private UUID playerId;

    public PacketRespawn(UUID playerId){
        this.playerId = playerId;
    }

    public PacketRespawn(){
    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.playerId = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        if(game.getWorld() != null){
            Entity entity = game.getWorld().getEntity(this.playerId);
            if(entity instanceof EntityPlayer){
                ((EntityPlayer)entity).resetAndSpawn(game);
            }
        }
    }
}
