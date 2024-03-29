package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ConstructionPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("construction");

    private ResourceName recipeName;
    private TileLayer machineLayer = null;
    private Pos2 machinePos = null;
    private int amount;

    public ConstructionPacket(ResourceName recipeName, TileEntity machine, int amount) {
        this.recipeName = recipeName;
        if (machine != null) {
            this.machineLayer = machine.layer;
            this.machinePos = new Pos2(machine.x, machine.y);
        }
        this.amount = amount;
    }

    public ConstructionPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        NetUtil.writeStringToBuffer(buf, this.recipeName.toString());
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
        this.recipeName = new ResourceName(NetUtil.readStringFromBuffer(buf));
        if (buf.readBoolean()) {
            this.machineLayer = TileLayer.getAllLayers().get(buf.readInt());
            this.machinePos = new Pos2(buf.readInt(), buf.readInt());
        }
        this.amount = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
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


    @Override
    public ResourceName getName() {
        return NAME;
    }
}
