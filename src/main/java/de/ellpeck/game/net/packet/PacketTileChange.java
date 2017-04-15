package de.ellpeck.game.net.packet;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.tile.Tile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.newdawn.slick.util.Log;

import java.io.IOException;

public class PacketTileChange implements IPacket{

    private int x;
    private int y;

    private TileLayer layer;
    private Tile tile;

    public PacketTileChange(int x, int y, TileLayer layer, Tile tile){
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.tile = tile;
    }

    public PacketTileChange(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.layer.ordinal());
        buf.writeShort(ContentRegistry.TILE_REGISTRY.getId(this.tile));
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.layer = TileLayer.LAYERS[buf.readInt()];
        this.tile = ContentRegistry.TILE_REGISTRY.get(buf.readShort());
    }

    @Override
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                Chunk chunk = game.world.getChunk(this.x, this.y);
                chunk.setTileInner(this.layer, this.x-chunk.x, this.y-chunk.y, this.tile, false);
            }
        });
    }
}
