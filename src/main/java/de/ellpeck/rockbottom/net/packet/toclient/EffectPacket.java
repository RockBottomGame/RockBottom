/*
 * This file ("PacketEffect.java") is part of the RockBottomAPI by Ellpeck.
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
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.effect.ActiveEffect;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class EffectPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("effect");

    private ActiveEffect effect;
    private boolean remove;

    public EffectPacket(ActiveEffect effect, boolean remove) {
        this.effect = effect;
        this.remove = remove;
    }

    public EffectPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        DataSet set = new DataSet();
        this.effect.save(set);
        NetUtil.writeSetToBuffer(set, buf);
        buf.writeBoolean(this.remove);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        DataSet set = new DataSet();
        NetUtil.readSetFromBuffer(set, buf);
        this.effect = ActiveEffect.load(set);
        this.remove = buf.readBoolean();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (this.effect != null) {
            AbstractPlayerEntity player = game.getPlayer();
            if (player != null) {
                if (this.remove) {
                    player.removeEffect(this.effect.getEffect());
                } else {
                    player.addEffect(this.effect);
                }
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
