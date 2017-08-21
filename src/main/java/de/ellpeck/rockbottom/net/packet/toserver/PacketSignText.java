package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySign;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketSignText implements IPacket{

    private int x;
    private int y;
    private String text;

    public PacketSignText(int x, int y, String text){
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public PacketSignText(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        NetUtil.writeStringToBuffer(this.text, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.text = NetUtil.readStringFromBuffer(buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            IWorld world = game.getWorld();
            if(world != null){
                TileEntitySign tile = world.getTileEntity(this.x, this.y, TileEntitySign.class);
                if(tile != null){
                    tile.text = this.text;

                    tile.sendToClients();
                    world.setDirty(this.x, this.y);
                }
            }
            return true;
        });
    }
}
