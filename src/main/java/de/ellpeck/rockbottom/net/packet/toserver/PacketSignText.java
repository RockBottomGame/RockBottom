package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySign;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketSignText implements IPacket{

    private int x;
    private int y;
    private String[] text;

    public PacketSignText(int x, int y, String[] text){
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public PacketSignText(){
    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeInt(this.x);
        buf.writeInt(this.y);

        for(int i = 0; i < this.text.length; i++){
            NetUtil.writeStringToBuffer(this.text[i], buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.x = buf.readInt();
        this.y = buf.readInt();

        this.text = new String[TileEntitySign.TEXT_AMOUNT];
        for(int i = 0; i < this.text.length; i++){
            this.text[i] = NetUtil.readStringFromBuffer(buf);
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        IWorld world = game.getWorld();
        if(world != null){
            TileEntitySign tile = world.getTileEntity(this.x, this.y, TileEntitySign.class);
            if(tile != null){
                System.arraycopy(this.text, 0, tile.text, 0, TileEntitySign.TEXT_AMOUNT);
                tile.sendToClients();
            }
        }
    }
}
