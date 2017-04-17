package de.ellpeck.game.net.packet.toclient;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.world.tile.Tile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketParticles implements IPacket{

    private int x;
    private int y;
    private int type;
    private int[] args;

    public PacketParticles(int x, int y, int type, int... args){
        this.x = x;
        this.y = y;
        this.type = type;
        this.args = args;
    }

    public PacketParticles(){

    }

    public static PacketParticles tile(int x, int y, Tile tile, int meta){
        return new PacketParticles(x, y, 0, ContentRegistry.TILE_REGISTRY.getId(tile), meta);
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.type);

        buf.writeInt(this.args.length);
        for(int arg : this.args){
            buf.writeInt(arg);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.type = buf.readInt();

        this.args = new int[buf.readInt()];
        for(int i = 0; i < this.args.length; i++){
            this.args[i] = buf.readInt();
        }
    }

    @Override
    public void handle(Game game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                if(this.type == 0){
                    Tile tile = ContentRegistry.TILE_REGISTRY.get(this.args[0]);
                    game.particleManager.addTileParticles(game.world, this.x, this.y, tile, this.args[1]);
                }
            }
            return true;
        });
    }
}
