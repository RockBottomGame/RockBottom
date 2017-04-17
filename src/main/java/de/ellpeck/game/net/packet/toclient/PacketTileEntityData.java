package de.ellpeck.game.net.packet.toclient;

import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.net.NetUtil;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.world.tile.entity.TileEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketTileEntityData implements IPacket{

    private int x;
    private int y;
    private final DataSet tileSet = new DataSet();

    public PacketTileEntityData(int x, int y, TileEntity tile){
        this.x = x;
        this.y = y;
        tile.saveSynced(this.tileSet);
    }

    public PacketTileEntityData(){

    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        NetUtil.writeSetToBuffer(this.tileSet, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.x = buf.readInt();
        this.y = buf.readInt();
        NetUtil.readSetFromBuffer(this.tileSet, buf);
    }

    @Override
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                TileEntity tile = game.world.getTileEntity(this.x, this.y);
                if(tile != null){
                    tile.loadSynced(this.tileSet);
                }
                return true;
            }
            else{
                return false;
            }
        });
    }
}
