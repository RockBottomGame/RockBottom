package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class InitialServerDataPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("initial_server_data");

    private final DataSet playerSet = new DataSet();
    private DataSet worldData;
    private WorldInfo info;
    private DynamicRegistryInfo regInfo;
    private ResourceName subName;

    public InitialServerDataPacket(AbstractPlayerEntity player, WorldInfo info, ResourceName subName, DataSet worldData, DynamicRegistryInfo regInfo) {
        player.save(this.playerSet, true);
        this.info = info;
        this.regInfo = regInfo;
        this.worldData = worldData;
        this.subName = subName;
    }

    public InitialServerDataPacket() {

    }

    @Override
    public void toBuffer(ByteBuf buf) {
        NetUtil.writeSetToBuffer(this.worldData, buf);
        NetUtil.writeSetToBuffer(this.playerSet, buf);
        this.info.toBuffer(buf);
        this.regInfo.toBuffer(buf);
        if (this.subName != null) {
            NetUtil.writeStringToBuffer(this.subName.toString(), buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.worldData = new DataSet();
        NetUtil.readSetFromBuffer(this.worldData, buf);
        NetUtil.readSetFromBuffer(this.playerSet, buf);

        this.info = new WorldInfo(null);
        this.info.fromBuffer(buf);

        this.regInfo = new DynamicRegistryInfo();
        this.regInfo.fromBuffer(buf);

        if (buf.isReadable()) {
            this.subName = new ResourceName(NetUtil.readStringFromBuffer(buf));
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getWorld() == null) {
            RockBottomAPI.logger().info("Received initial server data, joining world");

            game.joinWorld(this.playerSet, this.info, this.subName, this.worldData, this.regInfo);

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

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
