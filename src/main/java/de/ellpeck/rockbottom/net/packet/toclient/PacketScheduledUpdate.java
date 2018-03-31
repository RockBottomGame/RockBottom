package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketScheduledUpdate implements IPacket{

    private TileLayer layer;
    private int x;
    private int y;
    private int scheduledMeta;

    public PacketScheduledUpdate(TileLayer layer, int x, int y, int scheduledMeta){
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.scheduledMeta = scheduledMeta;
    }

    public PacketScheduledUpdate(){
    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeInt(this.layer.index());
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.scheduledMeta);
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.layer = TileLayer.getAllLayers().get(buf.readInt());
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.scheduledMeta = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        IWorld world = game.getWorld();
        if(world != null){
            TileState state = world.getState(this.x, this.y);
            state.getTile().onScheduledUpdate(world, this.x, this.y, this.layer, this.scheduledMeta);
        }
    }
}
