package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.newdawn.slick.util.Log;

import java.io.IOException;
import java.util.Set;

public class PacketChunk implements IPacket{

    private TileLayer[] layers;
    private int[] tileData;
    private byte[] lightData;
    private int chunkX;
    private int chunkY;

    public PacketChunk(IChunk chunk){
        this.chunkX = chunk.getGridX();
        this.chunkY = chunk.getGridY();

        Set<TileLayer> layers = chunk.getLoadedLayers();
        int amount = Constants.CHUNK_SIZE*Constants.CHUNK_SIZE*layers.size();
        this.tileData = new int[amount];
        this.lightData = new byte[amount*2];
        this.layers = layers.toArray(new TileLayer[layers.size()]);

        int index = 0;
        for(TileLayer layer : layers){
            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    this.tileData[index] = chunk.getWorld().getIdForState(chunk.getStateInner(layer, x, y));

                    this.lightData[index] = chunk.getSkylightInner(x, y);
                    this.lightData[amount+index] = chunk.getArtificialLightInner(x, y);

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

        buf.writeInt(this.layers.length);
        for(TileLayer layer : this.layers){
            buf.writeInt(layer.sessionIndex());
        }

        for(int tile : this.tileData){
            buf.writeInt(tile);
        }
        buf.writeBytes(this.lightData);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.chunkX = buf.readInt();
        this.chunkY = buf.readInt();

        this.layers = new TileLayer[buf.readInt()];
        for(int i = 0; i < this.layers.length; i++){
            this.layers[i] = TileLayer.getAllLayers().get(buf.readInt());
        }

        int amount = Constants.CHUNK_SIZE*Constants.CHUNK_SIZE*this.layers.length;

        this.tileData = new int[amount];
        for(int i = 0; i < this.tileData.length; i++){
            this.tileData[i] = buf.readInt();
        }

        this.lightData = new byte[amount*2];
        buf.readBytes(this.lightData);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getWorld() != null){
                RockBottomAPI.logger().config("Receiving chunk at "+this.chunkX+", "+this.chunkY);

                IChunk chunk = game.getWorld().getChunkFromGridCoords(this.chunkX, this.chunkY);
                chunk.setGenerating(true);

                int amount = Constants.CHUNK_SIZE*Constants.CHUNK_SIZE*this.layers.length;

                int index = 0;
                for(TileLayer layer : this.layers){
                    for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                        for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                            chunk.setStateInner(layer, x, y, chunk.getWorld().getStateForId(this.tileData[index]));

                            chunk.setSkylightInner(x, y, this.lightData[index]);
                            chunk.setArtificialLightInner(x, y, this.lightData[amount+index]);

                            index++;
                        }
                    }
                }

                chunk.setGenerating(false);

                return true;
            }
            else{
                return false;
            }
        });
    }
}
