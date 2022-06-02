package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.apiimpl.InternalHooks;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ShiftClickPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("shift_click");

    private int slotFrom;
    private int slotInto;

    public ShiftClickPacket() {
    }

    public ShiftClickPacket(int slotFrom, int slotInto) {
        this.slotFrom = slotFrom;
        this.slotInto = slotInto;
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.slotFrom);
        buf.writeInt(this.slotInto);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.slotFrom = buf.readInt();
        this.slotInto = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        if (player != null) {
            ItemContainer container = player.getContainer();
            if (container != null) {
                InternalHooks.shiftClick(player, container, this.slotFrom, this.slotInto);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
