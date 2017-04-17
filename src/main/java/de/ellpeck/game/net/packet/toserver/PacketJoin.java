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
        game.scheduleAction(() -> {
            if(game.world != null){
                if(game.world.getPlayer(this.id) == null){
                    EntityPlayer player = game.world.createPlayer(this.id, context.channel());
                    game.world.addEntity(player);
                    player.sendPacket(new PacketInitialServerData(player, game.world.info));

                    Log.info("Player with id "+this.id+" joined, sending initial server data");
                }
                else{
                    Log.warn("Player with id "+this.id+" tried joining while already connected!");
                }
            }
            return true;
        });
    }
}
