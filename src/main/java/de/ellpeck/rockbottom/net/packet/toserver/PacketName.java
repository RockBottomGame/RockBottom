package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.net.packet.toclient.PacketEntityChange;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketName implements IPacket{

    private UUID id;
    private String name;

    public PacketName(UUID id, String name){
        this.id = id;
        this.name = name;
    }

    public PacketName(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.id.getMostSignificantBits());
        buf.writeLong(this.id.getLeastSignificantBits());
        NetUtil.writeStringToBuffer(this.name, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.id = new UUID(buf.readLong(), buf.readLong());
        this.name = NetUtil.readStringFromBuffer(buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getWorld() != null){
                AbstractEntityPlayer player = game.getWorld().getPlayer(this.id);
                if(player != null){
                    player.setName(this.name);
                    RockBottomAPI.getNet().sendToAllPlayersExcept(game.getWorld(), new PacketEntityChange(player, false), player);
                }
            }
            return true;
        });
    }
}
