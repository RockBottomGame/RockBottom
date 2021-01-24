/*
 * This file ("PacketDamage.java") is part of the RockBottomAPI by Ellpeck.
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

package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.LivingEntity;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public final class DamagePacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("damage");

    private UUID entityId;
    private int damage;

    public DamagePacket(UUID entityId, int damage) {
        this.entityId = entityId;
        this.damage = damage;
    }

    public DamagePacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.entityId.getMostSignificantBits());
        buf.writeLong(this.entityId.getLeastSignificantBits());
        buf.writeInt(this.damage);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.entityId = new UUID(buf.readLong(), buf.readLong());
        this.damage = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        IWorld world = game.getWorld();
        if (world != null) {
            Entity entity = world.getEntity(this.entityId);
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).takeDamage(this.damage);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
