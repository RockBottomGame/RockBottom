package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.init.RockBottom;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketPlayerConnection implements IPacket{

    private String connectedName;
    private boolean disconnect;

    public PacketPlayerConnection(String connectedName, boolean disconnect){
        this.connectedName = connectedName;
        this.disconnect = disconnect;
    }

    public PacketPlayerConnection(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        NetUtil.writeStringToBuffer(this.connectedName, buf);
        buf.writeBoolean(this.disconnect);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.connectedName = NetUtil.readStringFromBuffer(buf);
        this.disconnect = buf.readBoolean();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            display(game, this.disconnect, this.connectedName);
            return true;
        });
    }

    public static void display(IGameInstance game, boolean disconnect, String name){
        String key = "info."+(disconnect ? "disconnect" : "connect");
        String message = game.getAssetManager().localize(RockBottom.internalRes(key), name);
        game.getChatLog().displayMessage(FormattingCode.ORANGE+message);
    }
}
