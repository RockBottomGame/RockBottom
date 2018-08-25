package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketTime implements IPacket {

    private int currentTime;
    private int totalTime;
    private boolean frozen;

    public PacketTime(int currentTime, int totalTime, boolean frozen) {
        this.currentTime = currentTime;
        this.totalTime = totalTime;
        this.frozen = frozen;
    }

    public PacketTime() {
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
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            world.setCurrentTime(this.currentTime);
            world.setTotalTime(this.totalTime);
            world.getWorldInfo().timeFrozen = this.frozen;
        }
    }
}
