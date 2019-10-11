package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.GuiItemList;
import de.ellpeck.rockbottom.gui.container.ContainerItemList;
import de.ellpeck.rockbottom.net.packet.backandforth.PacketOpenUnboundContainer;

public class CommandItemList extends Command {

    public CommandItemList() {
        super(ResourceName.intern("item_list"), "Opens a list of all items that you can take from freely", 5, "item_list", "items");
    }

    public static void open(AbstractEntityPlayer player) {
        player.openGuiContainer(new GuiItemList(player), new ContainerItemList(player));

        if (!player.world.isClient()) {
            IPacket packet = new PacketOpenUnboundContainer(null, PacketOpenUnboundContainer.ITEM_LIST_ID);
            player.sendPacket(packet);
        }
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
        if (sender instanceof AbstractEntityPlayer) {
            open((AbstractEntityPlayer) sender);
            return null;
        } else {
            return new ChatComponentText(FormattingCode.RED + "Only players can execute this command!");
        }
    }
}
