package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketScheduledUpdate implements IPacket{

    private TileLayer layer;
    private int x;
    private int y;

    public PacketScheduledUpdate(TileLayer layer, int x, int y){
        this.layer = layer;
        this.x = x;
        this.y = y;
    }

    public PacketScheduledUpdate(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.layer.ordinal());
        buf.writeInt(this.x);
        buf.writeInt(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.layer = TileLayer.LAYERS[buf.readInt()];
        this.x = buf.readInt();
        this.y = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            IWorld world = game.getWorld();
            if(world != null){
                TileState state = world.getState(this.x, this.y);
                state.getTile().onScheduledUpdate(world, this.x, this.y, this.layer);
            }

            return true;
        });
    }
}
