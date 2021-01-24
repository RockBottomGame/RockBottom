/*
 * This file ("PacketDrop.java") is part of the RockBottomAPI by Ellpeck.
 * View the source code at <https://github.com/RockBottomGame/>.
 * View information on the project at <https://rockbottom.ellpeck.de/>.
 *
 * The RockBottomAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The RockBottomAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the RockBottomAPI. If not, see <http://www.gnu.org/licenses/>.
 *
 * © 2018 Ellpeck
 */

package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.AbstractItemEntity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public final class DropPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("drop");

    private UUID playerId;

    public DropPacket() {
    }

    public DropPacket(UUID playerId) {
        this.playerId = playerId;
    }

    public static void dropHeldItem(AbstractPlayerEntity player, ItemContainer container) {
        if (container.holdingInst != null) {
            if (RockBottomAPI.getNet().isClient()) {
                RockBottomAPI.getNet().sendToServer(new DropPacket(player.getUniqueId()));
            } else {
                AbstractItemEntity.spawn(player.world, container.holdingInst, player.getX(), player.getY() + 1, player.facing.x * 0.25, 0);
            }
            container.holdingInst = null;
        }
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.playerId = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            AbstractPlayerEntity player = world.getPlayer(this.playerId);
            if (player != null) {
                ItemContainer container = player.getContainer();
                if (container != null) {
                    dropHeldItem(player, container);
                }
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
