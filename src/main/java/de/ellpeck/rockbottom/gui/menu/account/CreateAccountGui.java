package de.ellpeck.rockbottom.gui.menu.account;

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

public class CreateAccountGui extends Gui {

    private InputFieldComponent emailField;
    private InputFieldComponent passField;
    private InputFieldComponent repeatPassField;
    private InputFieldComponent usernameField;

    public CreateAccountGui(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        IGuiManager guiManager = game.getGuiManager();
        IAssetManager assetManager = game.getAssetManager();

        this.emailField = new InputFieldComponent(this, this.width / 2 - 75, 25, 150, 16, true, true, false, 256, false);
        this.components.add(this.emailField);

        this.passField = new InputFieldComponent(this, this.width / 2 - 75, 55, 150, 16, true, true, false, 256, false);
        this.passField.setCensored(true);
        this.components.add(this.passField);

        this.repeatPassField = new InputFieldComponent(this, this.width / 2 - 75, 85, 150, 16, true, true, false, 256, false);
        this.repeatPassField.setCensored(true);
        this.components.add(this.repeatPassField);

        this.usernameField = new InputFieldComponent(this, this.width / 2 - 75, 115, 70, 16, true, true, false, 256, false);
        this.components.add(this.usernameField);

        this.components.add(new ButtonComponent(this, this.width / 2 + 5, 115, 70, 16, () -> {
            CreateAccountGui.this.createAccount(game);
            return true;
        }, assetManager.localize(ResourceName.intern("button.create_account"))));

        this.components.add(new ButtonComponent(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
            guiManager.openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        IFont font = manager.getFont();
        font.drawCenteredString(this.width / 2f, 15, manager.localize(ResourceName.intern("info.email")), 0.35F, false);
        font.drawCenteredString(this.width / 2f, 45, manager.localize(ResourceName.intern("info.password")), 0.35F, false);
        font.drawCenteredString(this.width / 2f, 75, manager.localize(ResourceName.intern("info.repeat_password")), 0.35F, false);
        font.drawCenteredString(this.width / 2f - 40, 105, manager.localize(ResourceName.intern("info.username")), 0.35F, false);
    }

    private void createAccount(IGameInstance game) {
        IAssetManager assetManager = game.getAssetManager();

        ResponseGui responseGui = new ResponseGui(this);
        game.getGuiManager().openGui(responseGui);
        ManagementServerUtil.createUser(this.emailField.getText(), this.usernameField.getText(), this.passField.getText(),
                msg -> responseGui.displayResponse(assetManager.localize(ResourceName.intern(msg)), this.parent),
                msg -> responseGui.displayResponse(assetManager.localize(ResourceName.intern(msg)))
        );
    }


    @Override
    public ResourceName getName() {
        return ResourceName.intern("create_account");
    }
}
