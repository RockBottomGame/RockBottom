package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketInitialServerData implements IPacket {

    private final DataSet playerSet = new DataSet();
    private WorldInfo info;
    private DynamicRegistryInfo regInfo;

    public PacketInitialServerData(AbstractEntityPlayer player, WorldInfo info, DynamicRegistryInfo regInfo) {
        player.save(this.playerSet, true);
        this.info = info;
        this.regInfo = regInfo;
    }

    public PacketInitialServerData() {

    }

    @Override
    public void toBuffer(ByteBuf buf) {
        NetUtil.writeSetToBuffer(this.playerSet, buf);
        this.info.toBuffer(buf);
        this.regInfo.toBuffer(buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        NetUtil.readSetFromBuffer(this.playerSet, buf);

        this.info = new WorldInfo(null);
        this.info.fromBuffer(buf);

        this.regInfo = new DynamicRegistryInfo();
        this.regInfo.fromBuffer(buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getWorld() == null) {
            RockBottomAPI.logger().info("Received initial server data, joining world");

            game.joinWorld(this.playerSet, this.info, this.regInfo);

            IGuiManager gui = game.getGuiManager();
            gui.fadeOut(20, () -> {
                gui.closeGui();
                gui.updateDimensions();
                game.getToaster().cancelAllToasts();

                gui.fadeIn(20, null);
            });
        } else {
            RockBottomAPI.logger().warning("Received initial server data while already being in a world!");
            context.channel().disconnect();
        }
    }
}
