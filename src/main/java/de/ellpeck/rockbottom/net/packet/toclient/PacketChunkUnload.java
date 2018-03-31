package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketChunkUnload implements IPacket{

    private int gridX;
    private int gridY;

    public PacketChunkUnload(int gridX, int gridY){
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public PacketChunkUnload(){
    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeInt(this.gridX);
        buf.writeInt(this.gridY);
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.gridX = buf.readInt();
        this.gridY = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        IWorld world = game.getWorld();
        if(world != null){
            RockBottomAPI.logger().config("Unloading chunk at "+this.gridX+", "+this.gridY);

            if(world.isChunkLoaded(this.gridX, this.gridY)){
                IChunk chunk = world.getChunkFromGridCoords(this.gridX, this.gridY);
                world.unloadChunk(chunk);
            }
        }
    }
}
