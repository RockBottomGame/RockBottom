package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketBreakTile implements IPacket{

    private UUID playerId;
    private TileLayer layer;
    private double x;
    private double y;

    public PacketBreakTile(UUID playerId, TileLayer layer, double x, double y){
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
        buf.writeInt(this.layer.index());
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.layer = TileLayer.getAllLayers().get(buf.readInt());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        IWorld world = game.getWorld();
        if(world != null){
            AbstractEntityPlayer player = world.getPlayer(this.playerId);
            if(player != null && player.isInRange(this.x, this.y)){
                int x = Util.floor(this.x);
                int y = Util.floor(this.y);

                Tile tile = world.getState(this.layer, x, y).getTile();
                if(InteractionManager.defaultTileBreakingCheck(world, x, y, this.layer) && tile.canBreak(world, x, y, this.layer)){
                    boolean isRightTool = RockBottomAPI.getApiHandler().isToolEffective(player, player.getInv().get(player.getSelectedSlot()), tile, this.layer, x, y);
                    InteractionManager.breakTile(tile, player, x, y, this.layer, isRightTool);
                }
            }
        }
    }
}
