package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketRespawn implements IPacket{

    private UUID playerId;

    public PacketRespawn(UUID playerId){
        this.playerId = playerId;
    }

    public PacketRespawn(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getWorld() != null){
                Entity entity = game.getWorld().getEntity(this.playerId);
                if(entity != null && entity instanceof EntityPlayer){
                    ((EntityPlayer)entity).resetAndSpawn(game);
                }
            }
            return true;
        });
    }
}
