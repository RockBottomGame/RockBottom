package de.ellpeck.game.net.packet.toclient;

import de.ellpeck.game.Game;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.TileLayer;
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
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                Chunk chunk = game.world.getChunk(this.x, this.y);
                chunk.setMetaInner(this.layer, this.x-chunk.x, this.y-chunk.y, this.meta);
            }
            return true;
        });
    }
}
