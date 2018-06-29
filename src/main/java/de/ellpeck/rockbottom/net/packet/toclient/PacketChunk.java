package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class PacketChunk implements IPacket {

    private static final int TOTAL = Constants.CHUNK_SIZE * Constants.CHUNK_SIZE;
    private final byte[] lightData = new byte[TOTAL * 2];
    private final short[] biomeData = new short[TOTAL];
    private TileLayer[] layers;
    private int[] tileData;
    private int chunkX;
    private int chunkY;

    public PacketChunk(IChunk chunk) {
        this.chunkX = chunk.getGridX();
        this.chunkY = chunk.getGridY();

        List<TileLayer> layers = chunk.getLoadedLayers();
        this.tileData = new int[TOTAL * layers.size()];
        this.layers = layers.toArray(new TileLayer[0]);

        int index = 0;
        int lightIndex = 0;
        int biomeIndex = 0;

        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                for (TileLayer layer : this.layers) {
                    this.tileData[index] = chunk.getWorld().getIdForState(chunk.getStateInner(layer, x, y));
                    index++;
                }

                this.lightData[lightIndex] = chunk.getSkylightInner(x, y);
                this.lightData[TOTAL + lightIndex] = chunk.getArtificialLightInner(x, y);
                lightIndex++;

                this.biomeData[biomeIndex] = (short) chunk.getWorld().getIdForBiome(chunk.getBiomeInner(x, y));
                biomeIndex++;
            }
        }
    }

    public PacketChunk() {

    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkY);

        buf.writeInt(this.layers.length);
        for (TileLayer layer : this.layers) {
            buf.writeInt(layer.index());
        }

        for (int tile : this.tileData) {
            buf.writeInt(tile);
        }

        buf.writeBytes(this.lightData);

        for (short biome : this.biomeData) {
            buf.writeShort(biome);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.chunkX = buf.readInt();
        this.chunkY = buf.readInt();

        this.layers = new TileLayer[buf.readInt()];
        for (int i = 0; i < this.layers.length; i++) {
            this.layers[i] = TileLayer.getAllLayers().get(buf.readInt());
        }

        this.tileData = new int[TOTAL * this.layers.length];
        for (int i = 0; i < this.tileData.length; i++) {
            this.tileData[i] = buf.readInt();
        }

        buf.readBytes(this.lightData);

        for (int i = 0; i < this.biomeData.length; i++) {
            this.biomeData[i] = buf.readShort();
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getWorld() != null) {
            RockBottomAPI.logger().config("Receiving chunk at " + this.chunkX + ", " + this.chunkY);

            IChunk chunk = game.getWorld().getChunkFromGridCoords(this.chunkX, this.chunkY);
            chunk.setGenerating(true);

            int index = 0;
            int lightIndex = 0;
            int biomeIndex = 0;

            for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
                for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                    for (TileLayer layer : this.layers) {
                        chunk.setStateInner(layer, x, y, chunk.getWorld().getStateForId(this.tileData[index]));
                        index++;
                    }

                    chunk.setSkylightInner(x, y, this.lightData[lightIndex]);
                    chunk.setArtificialLightInner(x, y, this.lightData[TOTAL + lightIndex]);
                    lightIndex++;

                    chunk.setBiomeInner(x, y, chunk.getWorld().getBiomeForId(this.biomeData[biomeIndex]));
                    biomeIndex++;
                }
            }

            chunk.setGenerating(false);
        }
    }

    @Override
    public void enqueueAsAction(IGameInstance game, ChannelHandlerContext context) {
        game.enqueueAction(this::handle, context, inst -> inst.getWorld() != null);
    }
}
