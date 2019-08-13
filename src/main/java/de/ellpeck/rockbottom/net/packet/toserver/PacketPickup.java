package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketPickup implements IPacket{
    
    private UUID playerId;
    private TileLayer layer;
    private int x;
    private int y;
    
    public PacketPickup(UUID playerId, TileLayer layer, int x, int y) {
        this.playerId = playerId;
        this.layer = layer;
        this.x = x;
        this.y = y;
    }
    
    public PacketPickup() {
    
    }
    
    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.layer.index());
        buf.writeInt(this.x);
        buf.writeInt(this.y);
    }
    
    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.layer = TileLayer.getAllLayers().get(buf.readInt());
        this.x = buf.readInt();
        this.y = buf.readInt();
    }
    
    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        if (game.getWorld() != null) {
            AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
            if (player != null) {
                InteractionManager.pickup(player, this.layer, this.x, this.y);
            }
        }
    }
}
