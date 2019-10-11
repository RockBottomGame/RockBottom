package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketConstruction implements IPacket {

    private UUID playerId;
    private ResourceName recipeName;
    private TileLayer machineLayer = null;
    private Pos2 machinePos = null;
    private int amount;

    public PacketConstruction(UUID playerId, ResourceName recipeName, TileEntity machine, int amount) {
        this.playerId = playerId;
        this.recipeName = recipeName;
        if (machine != null) {
            this.machineLayer = machine.layer;
            this.machinePos = new Pos2(machine.x, machine.y);
        }
        this.amount = amount;
    }

    public PacketConstruction() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        NetUtil.writeStringToBuffer(this.recipeName.toString(), buf);
        buf.writeBoolean(machineLayer != null);
        if (machineLayer != null) {
            buf.writeInt(machineLayer.index());
            buf.writeInt(machinePos.getX());
            buf.writeInt(machinePos.getY());
        }
        buf.writeInt(this.amount);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.recipeName = new ResourceName(NetUtil.readStringFromBuffer(buf));
        if (buf.readBoolean()) {
            this.machineLayer = TileLayer.getAllLayers().get(buf.readInt());
            this.machinePos = new Pos2(buf.readInt(), buf.readInt());
        }
        this.amount = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getWorld() != null) {
            AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
            if (player != null) {
                ICompendiumRecipe recipe = ICompendiumRecipe.forName(this.recipeName);
                if (recipe instanceof PlayerCompendiumRecipe && recipe.isKnown(player)) {
                    PlayerCompendiumRecipe pcRecipe = (PlayerCompendiumRecipe) recipe;
                    TileEntity machine = null;
                    if (machineLayer != null) {
                        machine = player.world.getTileEntity(machineLayer, machinePos.getX(), machinePos.getY());
                    }
                    pcRecipe.playerConstruct(player, machine, this.amount);
                }
            }
        }
    }
}
