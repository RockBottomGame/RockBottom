package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.net.packet.IPacket;
import de.ellpeck.rockbottom.world.tile.entity.TileEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketTileEntityData implements IPacket{

    private int x;
    private int y;
    private final ByteBuf tileBuf = Unpooled.buffer();

    public PacketTileEntityData(int x, int y, TileEntity tile){
        this.x = x;
        this.y = y;
        tile.toBuf(this.tileBuf);
    }

    public PacketTileEntityData(){

    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.x);
        buf.writeInt(this.y);

        buf.writeInt(this.tileBuf.readableBytes());
        buf.writeBytes(this.tileBuf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.x = buf.readInt();
        this.y = buf.readInt();

        int readable = buf.readInt();
        buf.readBytes(this.tileBuf, readable);
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                TileEntity tile = game.world.getTileEntity(this.x, this.y);
                if(tile != null){
                    tile.fromBuf(this.tileBuf);
                }
                return true;
            }
            else{
                return false;
            }
        });
    }
}
