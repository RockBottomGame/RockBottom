package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;

public class TimePacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("time");

    private int currentTime;
    private int totalTime;
    private boolean frozen;

    public TimePacket(int currentTime, int totalTime, boolean frozen) {
        this.currentTime = currentTime;
        this.totalTime = totalTime;
        this.frozen = frozen;
    }

    public TimePacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.currentTime);
        buf.writeInt(this.totalTime);
        buf.writeBoolean(this.frozen);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.currentTime = buf.readInt();
        this.totalTime = buf.readInt();
        this.frozen = buf.readBoolean();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            world.setCurrentTime(this.currentTime);
            world.setTotalTime(this.totalTime);
            world.setTimeFrozen(this.frozen);
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
