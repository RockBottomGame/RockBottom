package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PlayerMovementPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("player_movement");

    private double x;
    private double y;
    private double motionX;
    private double motionY;
    private Direction facing;
    private boolean isFlying;

    public PlayerMovementPacket(double x, double y, double motionX, double motionY, Direction facing, boolean isFlying) {
        this.x = x;
        this.y = y;
        this.motionX = motionX;
        this.motionY = motionY;
        this.facing = facing;
        this.isFlying = isFlying;
    }

    public PlayerMovementPacket() {}

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.motionX);
        buf.writeDouble(this.motionY);
        buf.writeInt(this.facing.ordinal());
        buf.writeBoolean(this.isFlying);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.motionX = buf.readDouble();
        this.motionY = buf.readDouble();
        this.facing = Direction.DIRECTIONS[buf.readInt()];
        this.isFlying = buf.readBoolean();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        if (player != null) {
            player.motionX = this.motionX;
            player.motionY = this.motionY;
            player.facing = this.facing;
            player.setBoundsOrigin(this.x, this.y);
            player.isFlying = this.isFlying;
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
