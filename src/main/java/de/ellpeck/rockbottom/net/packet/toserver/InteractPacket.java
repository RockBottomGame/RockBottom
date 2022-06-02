package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class InteractPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("interact");

    private TileLayer layer;
    private double x;
    private double y;
    private boolean isDestroyKey;

    public InteractPacket(TileLayer layer, double x, double y, boolean isDestroyKey) {
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.isDestroyKey = isDestroyKey;
    }

    public InteractPacket() {}

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.layer.index());
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeBoolean(this.isDestroyKey);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.layer = TileLayer.getAllLayers().get(buf.readInt());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.isDestroyKey = buf.readBoolean();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        if (player != null) {
            InteractionManager.interact(player, this.layer, this.x, this.y, this.isDestroyKey);
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
