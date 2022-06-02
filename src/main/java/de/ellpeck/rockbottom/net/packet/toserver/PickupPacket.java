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

public class PickupPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("pickup");

    private TileLayer layer;
    private int x;
    private int y;

    public PickupPacket(TileLayer layer, int x, int y) {
        this.layer = layer;
        this.x = x;
        this.y = y;
    }

    public PickupPacket() {}

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.layer.index());
        buf.writeInt(this.x);
        buf.writeInt(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.layer = TileLayer.getAllLayers().get(buf.readInt());
        this.x = buf.readInt();
        this.y = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        if (player != null) {
            InteractionManager.pickup(player, this.layer, this.x, this.y);
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
