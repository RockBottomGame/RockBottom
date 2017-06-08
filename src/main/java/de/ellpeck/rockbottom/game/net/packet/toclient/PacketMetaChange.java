package de.ellpeck.rockbottom.game.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.TileLayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketMetaChange implements IPacket{

    private int x;
    private int y;

    private TileLayer layer;
    private int meta;

    public PacketMetaChange(int x, int y, TileLayer layer, int meta){
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.meta = meta;
    }

    public PacketMetaChange(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.layer.ordinal());
        buf.writeByte(this.meta);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.layer = TileLayer.LAYERS[buf.readInt()];
        this.meta = buf.readByte();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getWorld() != null){
                IChunk chunk = game.getWorld().getChunk(this.x, this.y);
                chunk.setMetaInner(this.layer, this.x-chunk.getX(), this.y-chunk.getY(), this.meta);
            }
            return true;
        });
    }
}
