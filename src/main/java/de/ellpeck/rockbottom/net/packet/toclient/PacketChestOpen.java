package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityChest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketChestOpen implements IPacket{

    private int x;
    private int y;
    private boolean open;

    public PacketChestOpen(int x, int y, boolean open){
        this.x = x;
        this.y = y;
        this.open = open;
    }

    public PacketChestOpen(){
    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeBoolean(this.open);
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.open = buf.readBoolean();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        IWorld world = game.getWorld();
        if(world != null){
            TileEntityChest chest = world.getTileEntity(this.x, this.y, TileEntityChest.class);
            if(chest != null){
                chest.setOpenCount(this.open ? 1 : 0);
                System.out.println("Chest is "+this.open);
            }
        }
    }
}
