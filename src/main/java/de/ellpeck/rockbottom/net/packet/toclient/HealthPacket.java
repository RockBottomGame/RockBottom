package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class HealthPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("health");

    private int health;
    private boolean isMax;
    private boolean isBreath;

    public HealthPacket(int health, boolean isMax, boolean isBreath) {
        this.health = health;
        this.isMax = isMax;
        this.isBreath = isBreath;
    }

    public HealthPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.health);
        buf.writeBoolean(this.isMax);
        buf.writeBoolean(this.isBreath);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.health = buf.readInt();
        this.isMax = buf.readBoolean();
        this.isBreath = buf.readBoolean();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        AbstractPlayerEntity player = game.getPlayer();
        if (player != null) {
            if (this.isMax) {
                if (this.isBreath)
                    player.setMaxBreath(this.health);
                else
                    player.setMaxHealth(this.health);
            } else {
                if (this.isBreath)
                    player.setBreath(this.health);
                else
                    player.setHealth(this.health);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
