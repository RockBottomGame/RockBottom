package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.render.design.PlayerDesign;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketPlayer implements IPacket {

    private final DataSet playerData = new DataSet();
    private UUID playerId;
    private boolean remove;

    public PacketPlayer(AbstractEntityPlayer player, boolean remove) {
        this.playerId = player.getUniqueId();
        this.remove = remove;

        if (!remove) {
            player.save(this.playerData, true);
            this.playerData.addString("design", Util.GSON.toJson(player.getDesign()));
        }
    }

    public PacketPlayer() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeBoolean(this.remove);
        NetUtil.writeSetToBuffer(this.playerData, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.remove = buf.readBoolean();
        NetUtil.readSetFromBuffer(this.playerData, buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            if (this.remove) {
                AbstractEntityPlayer player = world.getPlayer(this.playerId);
                if (player != null) {
                    world.removePlayer(player);
                }
            } else {
                PlayerDesign design = Util.GSON.fromJson(this.playerData.getString("design"), PlayerDesign.class);
                AbstractEntityPlayer player = new EntityPlayer(world, this.playerId, design);
                player.load(this.playerData, true);
                world.addPlayer(player);
            }
        }
    }
}