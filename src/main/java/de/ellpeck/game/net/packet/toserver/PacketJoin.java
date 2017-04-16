package de.ellpeck.game.net.packet.toserver;

import de.ellpeck.game.Game;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.net.packet.toclient.PacketInitialServerData;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.newdawn.slick.util.Log;

import java.io.IOException;
import java.util.UUID;

public class PacketJoin implements IPacket{

    private UUID id;

    public PacketJoin(UUID id){
        this.id = id;
    }

    public PacketJoin(){

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
    public void handle(Game game, ChannelHandlerContext context){
        System.out.println("Joining player, stack follows");
        Thread.dumpStack();
        game.scheduleAction(() -> {
            EntityPlayer player = game.world.addPlayer(this.id, true);
            player.setChannel(context.channel());

            player.sendPacket(new PacketInitialServerData(player, game.world.info));
            Log.info("Player with "+this.id+" joined, sending initial server data");
        });
    }
}
