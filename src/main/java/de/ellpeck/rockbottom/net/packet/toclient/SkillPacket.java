package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class SkillPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("skill");

    private float skillPercentage;
    private int skillPoints;

    public SkillPacket(float skillPercentage, int skillPoints) {
        this.skillPercentage = skillPercentage;
        this.skillPoints = skillPoints;
    }

    public SkillPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeFloat(this.skillPercentage);
        buf.writeInt(this.skillPoints);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.skillPercentage = buf.readFloat();
        this.skillPoints = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        AbstractPlayerEntity player = game.getPlayer();
        if (player != null) {
            player.setSkill(this.skillPercentage, this.skillPoints);
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
