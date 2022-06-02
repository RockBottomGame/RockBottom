package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class AttackPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("attack");

    private double x;
    private double y;

    public AttackPacket(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public AttackPacket() {}

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        if (player != null) {
            InteractionManager.attackEntity(player, this.x, this.y);
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
