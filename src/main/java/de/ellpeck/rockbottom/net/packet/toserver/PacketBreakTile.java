package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.net.packet.IPacket;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import de.ellpeck.rockbottom.world.tile.Tile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketBreakTile implements IPacket{

    private UUID playerId;
    private TileLayer layer;
    private int x;
    private int y;

    public PacketBreakTile(UUID playerId, TileLayer layer, int x, int y){
        this.playerId = playerId;
        this.layer = layer;
        this.x = x;
        this.y = y;
    }

    public PacketBreakTile(){

    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.layer.ordinal());
        buf.writeInt(this.x);
        buf.writeInt(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.layer = TileLayer.LAYERS[buf.readInt()];
        this.x = buf.readInt();
        this.y = buf.readInt();
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                Tile tile = game.world.getTile(this.layer, this.x, this.y);
                if(tile.canBreak(game.world, this.x, this.y, this.layer)){
                    EntityPlayer player = game.world.getPlayer(this.playerId);
                    boolean isRightTool = player != null && InteractionManager.getToolEffectiveness(player, player.inv.get(player.inv.selectedSlot), tile, this.layer, this.x, this.y) > 0;

                    game.world.destroyTile(this.x, this.y, this.layer, player, isRightTool);
                }
            }
            return true;
        });
    }
}
