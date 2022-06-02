package de.ellpeck.rockbottom.net.packet.backandforth;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.gui.menu.StatisticsGui;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class StatsPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("stats");

    private DataSet statisticsData;
    private boolean isRequest;

    public StatsPacket(DataSet statisticsData) {
        this.statisticsData = statisticsData;
    }

    public StatsPacket() {
        this.isRequest = true;
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeBoolean(this.isRequest);

        if (!this.isRequest) {
            NetUtil.writeSetToBuffer(this.statisticsData, buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.isRequest = buf.readBoolean();

        if (!this.isRequest) {
            this.statisticsData = new DataSet();
            NetUtil.readSetFromBuffer(this.statisticsData, buf);
        }
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        if (this.isRequest) {
            AbstractPlayerEntity player = context.getSender();
            if (player != null) {
                DataSet set = new DataSet();
                player.getStatistics().save(set);
                player.sendPacket(new StatsPacket(set));
            }
        } else {
            AbstractPlayerEntity player = game.getPlayer();
            if (player != null) {
                player.getStatistics().load(this.statisticsData);

                Gui gui = game.getGuiManager().getGui();
                if (gui instanceof StatisticsGui) {
                    ((StatisticsGui) gui).statsReceived = true;
                    gui.init(game);
                }
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
