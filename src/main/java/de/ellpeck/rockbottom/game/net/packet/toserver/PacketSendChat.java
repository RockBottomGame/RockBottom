package de.ellpeck.rockbottom.game.net.packet.toserver;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.net.NetUtil;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.game.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketSendChat implements IPacket{

    private UUID playerId;
    private String message;
    private String playerName;

    public PacketSendChat(UUID playerId, String message, String playerName){
        this.playerId = playerId;
        this.message = message;
        this.playerName = playerName;
    }

    public PacketSendChat(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        NetUtil.writeStringToBuffer(this.message, buf);
        NetUtil.writeStringToBuffer(this.playerName, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.message = NetUtil.readStringFromBuffer(buf);
        this.playerName = NetUtil.readStringFromBuffer(buf);
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                EntityPlayer player = game.world.getPlayer(this.playerId);
                if(player != null){
                    game.chatLog.sendPlayerMessage(this.message, player, this.playerName);
                }
            }
            return true;
        });
    }
}
