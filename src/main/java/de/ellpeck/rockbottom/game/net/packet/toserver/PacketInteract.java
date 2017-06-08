package de.ellpeck.rockbottom.game.net.packet.toserver;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.game.world.entity.player.InteractionManager;
import de.ellpeck.rockbottom.game.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketInteract implements IPacket{

    private UUID playerId;
    private TileLayer layer;
    private int x;
    private int y;

    public PacketInteract(UUID playerId, TileLayer layer, int x, int y){
        this.playerId = playerId;
        this.layer = layer;
        this.x = x;
        this.y = y;
    }

    public PacketInteract(){

    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.layer.ordinal());
        buf.writeInt(this.x);
        buf.writeInt(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.layer = TileLayer.LAYERS[buf.readInt()];
        this.x = buf.readInt();
        this.y = buf.readInt();
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                EntityPlayer player = game.world.getPlayer(this.playerId);
                if(player != null){
                    InteractionManager.interact(player, this.layer, this.x, this.y, false);
                }
            }
            return true;
        });
    }
}
