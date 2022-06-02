package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.entity.player.PlayerEntity;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class RespawnPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("respawn");

    private UUID playerId;
    private double x;
    private double y;

    public RespawnPacket(UUID playerId, double x, double y) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
    }

    public RespawnPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        if (game.getWorld() != null) {
            Entity entity = game.getWorld().getEntity(this.playerId);
            if (entity instanceof PlayerEntity) {
                ((PlayerEntity) entity).resetAndSpawn(game, this.x, this.y);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
