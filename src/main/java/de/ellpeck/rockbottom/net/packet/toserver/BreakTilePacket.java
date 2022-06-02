package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class BreakTilePacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("break_tile");

    private TileLayer layer;
    private double x;
    private double y;

    public BreakTilePacket(TileLayer layer, double x, double y) {
        this.layer = layer;
        this.x = x;
        this.y = y;
    }

    public BreakTilePacket() {}

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.layer.index());
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.layer = TileLayer.getAllLayers().get(buf.readInt());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        if (player != null) {
            int x = Util.floor(this.x);
            int y = Util.floor(this.y);

            Tile tile = player.world.getState(this.layer, x, y).getTile();
            ItemInstance instance = player.getSelectedItem();

            boolean isRightTool = InteractionManager.isToolEffective(player, instance, tile, this.layer, x, y);

            if (InteractionManager.defaultTileBreakingCheck(player.world, x, y, this.layer, this.x, this.y, player) && tile.canBreak(player.world, x, y, this.layer, player, isRightTool)) {
                InteractionManager.breakTile(tile, player, x, y, this.layer, isRightTool, instance);
            }
        }

    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
