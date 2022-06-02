package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import io.netty.buffer.ByteBuf;

public class TileChangePacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("tile_change");

    private int x;
    private int y;

    private TileLayer layer;
    private int tileId;

    public TileChangePacket(int x, int y, TileLayer layer, int tileId) {
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.tileId = tileId;
    }

    public TileChangePacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.layer.index());
        buf.writeShort(this.tileId);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.layer = TileLayer.getAllLayers().get(buf.readInt());
        this.tileId = buf.readShort();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        if (game.getWorld() != null) {
            if (game.getWorld().isPosLoaded(this.x, this.y)) {
                IChunk chunk = game.getWorld().getChunk(this.x, this.y);
                chunk.setStateInner(this.layer, this.x - chunk.getX(), this.y - chunk.getY(), game.getWorld().getStateForId(this.tileId));
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
