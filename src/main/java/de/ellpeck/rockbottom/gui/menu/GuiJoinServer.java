package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.GuiInformation;
import de.ellpeck.rockbottom.net.packet.toserver.PacketJoin;
import de.ellpeck.rockbottom.util.thread.ThreadHandler;

import java.util.logging.Level;

public class GuiJoinServer extends Gui {

    private ComponentInputField inputField;

    public GuiJoinServer(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.inputField = new ComponentInputField(this, this.width / 2 - 80, this.height / 2 - 40, 160, 16, true, false, true, 128, false);
        this.components.add(this.inputField);
        this.inputField.setText(game.getSettings().lastServerIp);

        this.components.add(new ComponentButton(this, this.width / 2 - 50, this.height / 2 - 20, 100, 16, () -> {
            Thread thread = new Thread(() -> {
                try {
                    String[] separated = this.inputField.getText().split(":");
                    if (separated.length == 1) {
                        RockBottomAPI.getNet().init(separated[0], 8000, false);
                    } else {
                        int port = Integer.parseInt(separated[1]);
                        RockBottomAPI.getNet().init(separated[0], port, false);
                    }

                    RockBottomAPI.logger().info("Attempting to join server");
                    RockBottomAPI.getNet().sendToServer(new PacketJoin(game.getUniqueId(), game.getPlayerDesign(), RockBottomAPI.getModLoader().getActiveMods()));
                } catch (Exception e) {
                    RockBottomAPI.logger().log(Level.WARNING, "Couldn't connect to server", e);
                    game.getGuiManager().openGui(new GuiInformation(this, 0.5F, true, game.getAssetManager().localize(ResourceName.intern("info.reject.connection"), e.getMessage())));
                }
            }, ThreadHandler.SERVER_JOIN);
            thread.start();

            game.getGuiManager().openGui(new GuiInformation(this, 0.5F, false, "Connecting..."));
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.connect"))));
        this.components.add(new ComponentButton(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.back"))));
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("join_server");
    }

    @Override
    public void onClosed(IGameInstance game) {
        super.onClosed(game);

        Settings settings = game.getSettings();
        String text = this.inputField.getText();
        if (!settings.lastServerIp.equals(text)) {
            settings.lastServerIp = text;
            settings.save();
        }
    }
}
