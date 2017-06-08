package de.ellpeck.rockbottom.game.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.net.packet.IPacket;
import de.ellpeck.rockbottom.game.world.Chunk;
import de.ellpeck.rockbottom.api.world.TileLayer;
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
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getWorld() != null){
                if(game.getWorld().isPosLoaded(this.x, this.y)){
                    IChunk chunk = game.getWorld().getChunk(this.x, this.y);
                    chunk.setTileInner(this.layer, this.x-chunk.getX(), this.y-chunk.getY(), game.getWorld().getTileForId(this.tileId), this.meta);
                }
            }
            return true;
        });
    }
}
