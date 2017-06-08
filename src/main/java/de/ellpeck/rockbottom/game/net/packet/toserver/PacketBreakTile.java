package de.ellpeck.rockbottom.game.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
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
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            IWorld world = game.getWorld();
            if(world != null){
                Tile tile = world.getTile(this.layer, this.x, this.y);
                if(tile.canBreak(world, this.x, this.y, this.layer)){
                    AbstractEntityPlayer player = world.getPlayer(this.playerId);

                    boolean isRightTool = player != null && RockBottomAPI.getGame().getInteractionManager().isToolEffective(player, player.getInv().get(player.getSelectedSlot()), tile, this.layer, this.x, this.y);
                    tile.doBreak(world, this.x, this.y, this.layer, player, isRightTool);
                }
            }
            return true;
        });
    }
}
