package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.ItemListGui;
import de.ellpeck.rockbottom.gui.container.ItemListContainer;
import de.ellpeck.rockbottom.net.packet.backandforth.OpenUnboundContainerPacket;

public class ItemListCommand extends Command {

    public ItemListCommand() {
        super(ResourceName.intern("item_list"), "Opens a list of all items that you can take from freely", 5, "item_list", "items");
    }

    public static void open(AbstractPlayerEntity player) {
        player.openGuiContainer(new ItemListGui(player), new ItemListContainer(player));

        if (!player.world.isClient()) {
            IPacket packet = new OpenUnboundContainerPacket(null, OpenUnboundContainerPacket.ITEM_LIST_ID);
            player.sendPacket(packet);
        }
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
        if (sender instanceof AbstractPlayerEntity) {
            open((AbstractPlayerEntity) sender);
            return null;
        } else {
            return new TextChatComponent(FormattingCode.RED + "Only players can execute this command!");
        }
    }
}
