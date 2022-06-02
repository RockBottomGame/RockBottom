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
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public final class DropPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("drop");

    public static void dropHeldItem(AbstractPlayerEntity player, ItemContainer container) {
        if (container.holdingInst != null) {
            if (RockBottomAPI.getNet().isClient()) {
                RockBottomAPI.getNet().sendToServer(new DropPacket());
            } else {
                AbstractItemEntity.spawn(player.world, container.holdingInst, player.getX(), player.getY() + 1, player.facing.x * 0.25, 0);
            }
            container.holdingInst = null;
        }
    }

    @Override
    public void toBuffer(ByteBuf buf) {
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        AbstractPlayerEntity player = context.getSender();
        if (player != null) {
            ItemContainer container = player.getContainer();
            if (container != null) {
                dropHeldItem(player, container);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
