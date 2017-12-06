package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.gui.GuiInformation;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketReject implements IPacket{

    private ChatComponent text;

    public PacketReject(ChatComponentTranslation text){
        this.text = text;
    }

    public PacketReject(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        DataSet set = new DataSet();
        this.text.save(set);
        NetUtil.writeSetToBuffer(set, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        DataSet set = new DataSet();
        NetUtil.readSetFromBuffer(set, buf);
        this.text = ChatComponentText.createFromSet(set);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        context.disconnect();
        game.quitWorld();

        IGuiManager manager = game.getGuiManager();
        manager.openGui(new GuiInformation(manager.getGui(), 0.5F, this.text.getDisplayWithChildren(game, game.getAssetManager())));
    }
}
