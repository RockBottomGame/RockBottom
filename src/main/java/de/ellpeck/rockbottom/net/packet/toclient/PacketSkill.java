package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketSkill implements IPacket {

    private float skillPercentage;
    private int skillPoints;

    public PacketSkill(float skillPercentage, int skillPoints) {
        this.skillPercentage = skillPercentage;
        this.skillPoints = skillPoints;
    }

    public PacketSkill() {
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
        AbstractEntityPlayer player = game.getPlayer();
        if (player != null) {
            player.setSkill(this.skillPercentage, this.skillPoints);
        }
    }
}
