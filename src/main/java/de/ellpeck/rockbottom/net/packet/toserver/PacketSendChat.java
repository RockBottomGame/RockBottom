package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.NetUtil;
import de.ellpeck.rockbottom.net.packet.IPacket;
import de.ellpeck.rockbottom.net.packet.toclient.PacketChatMessage;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketSendChat implements IPacket{

    private UUID playerId;
    private String message;

    public PacketSendChat(UUID playerId, String message){
        this.playerId = playerId;
        this.message = message;
    }

    public PacketSendChat(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        NetUtil.writeStringToBuffer(this.message, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.message = NetUtil.readStringFromBuffer(buf);
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(()->{
            if(game.world != null){
                EntityPlayer player = game.world.getPlayer(this.playerId);

                game.chatLog.addMessage(this.message);
                NetHandler.sendToAllPlayersExcept(game.world, new PacketChatMessage(this.message), player);
            }
            return true;
        });
    }
}
