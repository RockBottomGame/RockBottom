package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.gui.component.InputFieldComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.auth.ManagementServer;
import de.ellpeck.rockbottom.auth.ManagementServerUtil;
import de.ellpeck.rockbottom.auth.ServerResponse;
import de.ellpeck.rockbottom.gui.InformationGui;
import de.ellpeck.rockbottom.net.packet.toserver.JoinPacket;
import de.ellpeck.rockbottom.util.thread.ThreadHandler;

import java.util.logging.Level;

public class JoinServerGui extends Gui {

    private InputFieldComponent inputField;

    public JoinServerGui(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.inputField = new InputFieldComponent(this, this.width / 2 - 80, this.height / 2 - 40, 160, 16, true, false, true, 128, false);
        this.components.add(this.inputField);
        this.inputField.setText(game.getSettings().lastServerIp);

        this.components.add(new ButtonComponent(this, this.width / 2 - 50, this.height / 2 - 20, 100, 16, () -> {
            Thread thread = new Thread(() -> {
                RockBottomAPI.logger().info("Attempting to join server");
                ManagementServerUtil.checkVerificationStatus(ManagementServer.getServer().getApiToken(),
                        na -> {
                            ManagementServerUtil.getUser(ManagementServer.getServer().getApiToken(),
                                    account -> {
                                        // Try connect to the server
                                        try {
                                            String[] separated = this.inputField.getText().split(":");
                                            if (separated.length == 1) {
                                                RockBottomAPI.getNet().init(separated[0], 8000, false);
                                            } else {
                                                int port = Integer.parseInt(separated[1]);
                                                RockBottomAPI.getNet().init(separated[0], port, false);
                                            }
                                            RockBottomAPI.getNet().sendToServer(new JoinPacket(account, RockBottomAPI.getModLoader().getActiveMods()));
                                            game.setAccount(account); // Set fresh account data
                                        } catch (Exception e) {
                                            RockBottomAPI.logger().log(Level.WARNING, "Couldn't connect to server", e);
                                            game.getGuiManager().openGui(new InformationGui(this, 0.5F, true, game.getAssetManager().localize(ResourceName.intern("info.reject.connection"), e.getMessage())));
                                        }
                                    },
                                    msg -> {
                                        RockBottomAPI.logger().log(Level.WARNING, "Could not get fresh user data from the management server");
                                        game.getGuiManager().openGui(new InformationGui(this, 0.5f, true, game.getAssetManager().localize(ResourceName.intern(msg))));
                                    });
                        },
                        msg -> {
                            throw new IllegalStateException(game.getAssetManager().localize(ResourceName.intern("info.reject.not_verified")));
                        });
            }, ThreadHandler.SERVER_JOIN);
            thread.start();

            game.getGuiManager().openGui(new InformationGui(this, 0.5F, false, "Connecting..."));
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.connect"))));
        this.components.add(new ButtonComponent(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
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
