package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.net.packet.IPacket;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.tile.Tile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketTileChange implements IPacket{

    private int x;
    private int y;

    private TileLayer layer;
    private int tileId;
    private int meta;

    public PacketTileChange(int x, int y, TileLayer layer, int tileId, int meta){
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.tileId = tileId;
        this.meta = meta;
    }

    public PacketTileChange(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.layer.ordinal());
        buf.writeShort(this.tileId);
        buf.writeByte(this.meta);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.layer = TileLayer.LAYERS[buf.readInt()];
        this.tileId = buf.readShort();
        this.meta = buf.readByte();
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                if(game.world.isPosLoaded(this.x, this.y)){
                    Chunk chunk = game.world.getChunk(this.x, this.y);
                    chunk.setTileInner(this.layer, this.x-chunk.x, this.y-chunk.y, game.world.getTileForId(this.tileId), this.meta);
                }
            }
            return true;
        });
    }
}
