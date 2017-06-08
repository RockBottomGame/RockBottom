package de.ellpeck.rockbottom.game.net.packet.toclient;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.net.packet.IPacket;
import de.ellpeck.rockbottom.game.world.Chunk;
import de.ellpeck.rockbottom.api.world.TileLayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.newdawn.slick.util.Log;

import java.io.IOException;

public class PacketChunk implements IPacket{

    private static final int DATA_SIZE = Constants.CHUNK_SIZE*Constants.CHUNK_SIZE*TileLayer.LAYERS.length;
    private final short[] tileData = new short[DATA_SIZE];
    private final byte[] metaData = new byte[DATA_SIZE];
    private final byte[] lightData = new byte[DATA_SIZE*2];
    private int chunkX;
    private int chunkY;

    public PacketChunk(IChunk chunk){
        this.chunkX = chunk.getGridX();
        this.chunkY = chunk.getGridY();

        int index = 0;
        for(int i = 0; i < TileLayer.LAYERS.length; i++){
            TileLayer layer = TileLayer.LAYERS[i];

            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    this.tileData[index] = (short)chunk.getWorld().getIdForTile(chunk.getTileInner(layer, x, y));
                    this.metaData[index] = chunk.getMetaInner(layer, x, y);

                    this.lightData[index] = chunk.getSkylightInner(x, y);
                    this.lightData[DATA_SIZE+index] = chunk.getArtificialLightInner(x, y);

                    index++;
                }
            }
        }
    }

    public PacketChunk(){

    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkY);

        for(short tile : this.tileData){
            buf.writeShort(tile);
        }
        buf.writeBytes(this.metaData);
        buf.writeBytes(this.lightData);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.chunkX = buf.readInt();
        this.chunkY = buf.readInt();

        for(int i = 0; i < this.tileData.length; i++){
            this.tileData[i] = buf.readShort();
        }
        buf.readBytes(this.metaData);
        buf.readBytes(this.lightData);
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                Log.debug("Receiving chunk at "+this.chunkX+", "+this.chunkY);

                Chunk chunk = game.world.getChunkFromGridCoords(this.chunkX, this.chunkY);
                chunk.isGenerating = true;

                int index = 0;
                for(int i = 0; i < TileLayer.LAYERS.length; i++){
                    TileLayer layer = TileLayer.LAYERS[i];

                    for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                        for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                            chunk.setTileInner(layer, x, y, chunk.getWorld().getTileForId(this.tileData[index]), this.metaData[index]);

                            chunk.setSkylightInner(x, y, this.lightData[index]);
                            chunk.setArtificialLightInner(x, y, this.lightData[DATA_SIZE+index]);

                            index++;
                        }
                    }
                }

                chunk.isGenerating = false;

                return true;
            }
            else{
                return false;
            }
        });
    }
}
