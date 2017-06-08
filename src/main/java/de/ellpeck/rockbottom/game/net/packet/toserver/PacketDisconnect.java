package de.ellpeck.rockbottom.game.net.packet.toserver;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.net.packet.IPacket;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.newdawn.slick.util.Log;

import java.io.IOException;
import java.util.UUID;

public class PacketDisconnect implements IPacket{

    private UUID id;

    public PacketDisconnect(UUID id){
        this.id = id;
    }

    public PacketDisconnect(){

    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.id.getMostSignificantBits());
        buf.writeLong(this.id.getLeastSignificantBits());
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.id = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            EntityPlayer player = game.world.getPlayer(this.id);

            game.world.savePlayer(player);
            game.world.removeEntity(player);

            Log.info("Saving and removing disconnected player with id "+this.id+" from world");

            return true;
        });
    }
}
