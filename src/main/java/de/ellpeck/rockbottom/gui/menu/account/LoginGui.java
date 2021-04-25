package de.ellpeck.rockbottom.gui.menu.account;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.gui.component.InputFieldComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.auth.ManagementServerUtil;
import de.ellpeck.rockbottom.gui.menu.MainMenuGui;

public class LoginGui extends Gui {

    private InputFieldComponent emailField;
    private InputFieldComponent passField;

    public LoginGui(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        IGuiManager guiManager = game.getGuiManager();
        IAssetManager assetManager = game.getAssetManager();

        this.emailField = new InputFieldComponent(this, this.width / 2 - 75, 45, 150, 16, true, true, false, 256, false);
        this.components.add(this.emailField);

        this.passField = new InputFieldComponent(this, this.width / 2 - 75, 75, 150, 16, true, true, false, 256, false);
        this.passField.setCensored(true);
        this.components.add(this.passField);

        this.components.add(new ButtonComponent(this, 5, 5, 50, 16, () -> {
            guiManager.openGui(new CreateAccountGui(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.create_account"))));

        this.components.add(new ButtonComponent(this, this.width / 2 - 75, 100, 70, 16, () -> {
            this.login(game);
            return true;
        }, assetManager.localize(ResourceName.intern("button.login"))));

        this.components.add(new ButtonComponent(this, this.width / 2 + 5, 100, 70, 16, () -> {
            guiManager.openGui(new ForgotPasswordGui(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.forgot_password"))));

        this.components.add(new ButtonComponent(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
            guiManager.openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        IFont font = manager.getFont();
        font.drawCenteredString(this.width / 2f, 35, manager.localize(ResourceName.intern("info.email")), 0.35F, false);
        font.drawCenteredString(this.width / 2f, 65, manager.localize(ResourceName.intern("info.password")), 0.35F, false);
    }

    private void login(IGameInstance game) {
        ResponseGui responseGui = new ResponseGui(this);
        game.getGuiManager().openGui(responseGui);
        ManagementServerUtil.loginUser(this.emailField.getText(), this.passField.getText(),
                account -> {
                    if (!account.isVerified()) {
                        game.getGuiManager().openGui(new VerifyAccountGui(this, account));
                    } else {
                        game.setAccount(account);
                        game.getGuiManager().openGui(new MainMenuGui());
                    }
                },
                msg -> responseGui.displayResponse(game.getAssetManager().localize(ResourceName.intern(msg))));
    }

    private void forgotPassword() {

    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("login");
    }
}
