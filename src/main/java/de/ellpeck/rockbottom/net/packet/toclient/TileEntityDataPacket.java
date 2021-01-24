/*
 * This file ("PacketTileEntityData.java") is part of the RockBottomAPI by Ellpeck.
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
 * © 2017 Ellpeck
 */

package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public final class TileEntityDataPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("tile_entity_data");

    private final DataSet set = new DataSet();
    private int x;
    private int y;
    private TileLayer layer;

    public TileEntityDataPacket(int x, int y, TileLayer layer, TileEntity tile) {
        this.x = x;
        this.y = y;
        this.layer = layer;
        tile.save(this.set, true);
    }

    public TileEntityDataPacket() {

    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.layer.index());
        NetUtil.writeSetToBuffer(this.set, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.layer = TileLayer.getAllLayers().get(buf.readInt());
        NetUtil.readSetFromBuffer(this.set, buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        if (game.getWorld() != null) {
            TileEntity tile = game.getWorld().getTileEntity(this.layer, this.x, this.y);
            if (tile != null) {
                tile.load(this.set, true);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
