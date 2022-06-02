package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.net.IPacketContext;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TranslationChatComponent;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.InformationGui;
import io.netty.buffer.ByteBuf;

public class RejectPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("reject");

    private ChatComponent text;

    public RejectPacket(TranslationChatComponent text) {
        this.text = text;
    }

    public RejectPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        DataSet set = new DataSet();
        this.text.save(set);
        NetUtil.writeSetToBuffer(set, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        DataSet set = new DataSet();
        NetUtil.readSetFromBuffer(set, buf);
        this.text = TextChatComponent.createFromSet(set);
    }

    @Override
    public void handle(IGameInstance game, IPacketContext context) {
        context.getChannelContext().disconnect();
        game.quitWorld();

        IGuiManager manager = game.getGuiManager();
        manager.openGui(new InformationGui(manager.getGui(), 0.5F, true, this.text.getDisplayWithChildren(game, game.getAssetManager())));
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
