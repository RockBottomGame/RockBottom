package de.ellpeck.rockbottom.game.net.packet.toclient;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.net.packet.IPacket;
import de.ellpeck.rockbottom.game.world.Chunk;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.newdawn.slick.util.Log;

import java.io.IOException;

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
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.gridX);
        buf.writeInt(this.gridY);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.gridX = buf.readInt();
        this.gridY = buf.readInt();
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                Log.debug("Unloading chunk at "+this.gridX+", "+this.gridY);

                if(game.world.isChunkLoaded(this.gridX, this.gridY)){
                    Chunk chunk = game.world.getChunkFromGridCoords(this.gridX, this.gridY);
                    game.world.unloadChunk(chunk);
                }
            }
            return true;
        });
    }
}
