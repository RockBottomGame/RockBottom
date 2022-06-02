package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;

public class SoundPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("sound");

    private ResourceName soundName;
    private float pitch;
    private float volume;
    private boolean isBroadcast;
    private double x;
    private double y;
    private double z;

    public SoundPacket(ResourceName soundName, float pitch, float volume) {
        this.soundName = soundName;
        this.pitch = pitch;
        this.volume = volume;
        this.isBroadcast = true;
    }

    public SoundPacket(ResourceName soundName, double x, double y, double z, float pitch, float volume) {
        this.soundName = soundName;
        this.pitch = pitch;
        this.volume = volume;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SoundPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        NetUtil.writeStringToBuffer(buf, this.soundName.toString());
        buf.writeFloat(this.pitch);
        buf.writeFloat(this.volume);
        buf.writeBoolean(this.isBroadcast);
        if (!this.isBroadcast) {
            buf.writeDouble(this.x);
            buf.writeDouble(this.y);
            buf.writeDouble(this.z);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.soundName = new ResourceName(NetUtil.readStringFromBuffer(buf));
        this.pitch = buf.readFloat();
        this.volume = buf.readFloat();
        this.isBroadcast = buf.readBoolean();
        if (!this.isBroadcast) {
            this.x = buf.readDouble();
            this.y = buf.readDouble();
            this.z = buf.readDouble();
        }
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            if (this.isBroadcast) {
                world.broadcastSound(this.soundName, this.pitch, this.volume);
            } else {
                world.playSound(this.soundName, this.x, this.y, this.z, this.pitch, this.volume);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
