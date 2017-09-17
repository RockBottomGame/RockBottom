package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketInteract implements IPacket{

    private UUID playerId;
    private TileLayer layer;
    private double x;
    private double y;

    public PacketInteract(UUID playerId, TileLayer layer, double x, double y){
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
        buf.writeInt(this.layer.sessionIndex());
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.layer = TileLayer.getAllLayers().get(buf.readInt());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getWorld() != null){
                AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
                if(player != null && player.isInRange(this.x, this.y)){
                    InteractionManager.interact(player, this.layer, this.x, this.y);
                }
            }
            return true;
        });
    }
}
