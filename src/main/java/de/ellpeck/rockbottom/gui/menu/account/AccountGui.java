package de.ellpeck.rockbottom.gui.menu.account;

import de.ellpeck.rockbottom.api.IGameAccount;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.gui.component.InputFieldComponent;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.auth.ManagementServer;
import de.ellpeck.rockbottom.auth.ManagementServerUtil;
import de.ellpeck.rockbottom.gui.PlayerEditorGui;
import de.ellpeck.rockbottom.render.entity.PlayerEntityRenderer;

public class AccountGui extends Gui {

    private int previewType;

    private InputFieldComponent nameField;

    private InputFieldComponent passField;
    private InputFieldComponent repeatPassField;
    private InputFieldComponent oldPassField;

    public AccountGui(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.previewType = Util.RANDOM.nextInt(4);

        IGuiManager guiManager = game.getGuiManager();
        IAssetManager assetManager = game.getAssetManager();

        IGameAccount account = game.getAccount();

        if (account == null) {
            guiManager.openGui(this.parent);
            return;
        }

        // Username
        this.nameField = new InputFieldComponent(this, this.width / 2 - 75, 25, 150, 16, true, true, false, 256, false);
        this.components.add(this.nameField);

        this.components.add(new ButtonComponent(this, this.width / 2 + 85, 25, 60, 16, () -> {
            this.changeName(game);
            return true;
        }, assetManager.localize(ResourceName.intern("button.change_username"))));

        // Password
        this.passField = new InputFieldComponent(this, this.width / 2 - 75, 60, 150, 16, true, true, false, 256, false);
        this.passField.setCensored(true);
        this.components.add(this.passField);

        this.repeatPassField = new InputFieldComponent(this, this.width / 2 - 75, 90, 150, 16, true, true, false, 256, false);
        this.repeatPassField.setCensored(true);
        this.components.add(this.repeatPassField);

        this.oldPassField = new InputFieldComponent(this, this.width / 2 - 75, 120, 150, 16, true, true, false, 256, false);
        this.oldPassField.setCensored(true);
        this.components.add(this.oldPassField);

        this.components.add(new ButtonComponent(this, this.width / 2 + 85, 60, 60, 16, () -> {
            this.changePassword(game);
            return true;
        }, assetManager.localize(ResourceName.intern("button.change_password"))));

        // Edit Player
        this.components.add(new ButtonComponent(this, 10, this.height - 30, 80, 16, () -> {
            guiManager.openGui(new PlayerEditorGui(this, account.getPlayerDesign(), this.previewType));
            return true;
        }, assetManager.localize(ResourceName.intern("button.player_editor"))));

        this.components.add(new ButtonComponent(this, this.width - 90, this.height - 30, 80, 16, () -> {
            // TODO Invalidate token
            ManagementServer.getServer().removeApiToken();
            game.setAccount(null);
            game.getGuiManager().openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.logout"))));

        // Back
        this.components.add(new ButtonComponent(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
            guiManager.openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer renderer) {
        super.render(game, manager, renderer);

        IFont font = manager.getFont();
        float scale = 0.35F;
        font.drawCenteredString(46.5f, 25, game.getAccount().getDisplayName(), scale, true);

        PlayerEntityRenderer.renderPlayer(null, game, manager, renderer, game.getAccount().getPlayerDesign(), 24, 25 + font.getHeight(scale), 45F, this.previewType, Colors.WHITE);

        font.drawCenteredString(this.width / 2f, 15, manager.localize(ResourceName.intern("info.username")), scale, false);

        font.drawCenteredString(this.width / 2f, 50, manager.localize(ResourceName.intern("info.new_password")), scale, false);
        font.drawCenteredString(this.width / 2f, 80, manager.localize(ResourceName.intern("info.repeat_password")), scale, false);
        font.drawCenteredString(this.width / 2f, 110, manager.localize(ResourceName.intern("info.password")), scale, false);
    }

    private void changeName(IGameInstance game) {
        IAssetManager assetManager = game.getAssetManager();
        ResponseGui responseGui = new ResponseGui(this);
        game.getGuiManager().openGui(responseGui);
        ManagementServerUtil.setUsername(ManagementServer.getServer().getApiToken(), this.nameField.getText(),
                msg -> {
                    game.getAccount().setDisplayName(this.nameField.getText());
                    game.getGuiManager().openGui(this);
                },
                msg -> responseGui.displayResponse(assetManager.localize(ResourceName.intern(msg)))
        );

    }

    private void changePassword(IGameInstance game) {
        IAssetManager assetManager = game.getAssetManager();
        ResponseGui responseGui = new ResponseGui(this);
        game.getGuiManager().openGui(responseGui);
        if (!this.passField.getText().equals(this.repeatPassField.getText())) {
            responseGui.displayResponse(game.getAssetManager().localize(ResourceName.intern("info.server.password_mismatch")));
        } else {
            ManagementServerUtil.setPassword(ManagementServer.getServer().getApiToken(), this.oldPassField.getText(), this.passField.getText(),
                    msg -> responseGui.displayResponse(game.getAssetManager().localize(ResourceName.intern(msg))),
                    msg -> responseGui.displayResponse(game.getAssetManager().localize(ResourceName.intern(msg))));
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("account");
    }
}
