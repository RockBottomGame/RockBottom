package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;

public class ChangeWorldPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("change_world");

    private ResourceName subName;
    private DataSet worldData;

    public ChangeWorldPacket(DataSet worldData, ResourceName subName) {
        this.worldData = worldData;
        this.subName = subName;
    }

    public ChangeWorldPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        NetUtil.writeSetToBuffer(this.worldData, buf);
        if (this.subName != null) {
            NetUtil.writeStringToBuffer(buf, this.subName.toString());
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.worldData = new DataSet();
        NetUtil.readSetFromBuffer(this.worldData, buf);
        if (buf.isReadable()) {
            this.subName = new ResourceName(NetUtil.readStringFromBuffer(buf));
        }
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            game.changeWorld(this.subName, this.worldData);
            RockBottomAPI.logger().fine("Travelling to different world");
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
