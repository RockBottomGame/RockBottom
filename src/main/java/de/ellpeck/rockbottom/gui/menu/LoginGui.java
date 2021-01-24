package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.gui.component.InputFieldComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class LoginGui extends Gui {

    private InputFieldComponent nameField;
    private InputFieldComponent passField;
    private ButtonComponent loginButton;
    private ButtonComponent logoutButton;

    public LoginGui(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        IGuiManager guiManager = game.getGuiManager();
        IAssetManager assetManager = game.getAssetManager();

        this.nameField = new InputFieldComponent(this, this.width / 2 - 75, 25, 150, 16, true, true, false, 256, false);
        this.components.add(this.nameField);

        this.passField = new InputFieldComponent(this, this.width / 2 - 75, 55, 150, 16, true, true, false, 256, false);
        this.passField.setCensored(true);
        this.components.add(this.passField);

        this.loginButton = new ButtonComponent(this, this.width / 2 - 50, 80, 100, 16, () -> {
            return true;
        }, assetManager.localize(ResourceName.intern("button.login")));
        this.components.add(this.loginButton);

        this.logoutButton = new ButtonComponent(this, this.width / 2 - 50, 80, 100, 16, () -> {
            return true;
        }, assetManager.localize(ResourceName.intern("button.logout")));
        this.components.add(this.logoutButton);

        this.updateButtons();

        this.components.add(new ButtonComponent(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
            guiManager.openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        IFont font = manager.getFont();
        font.drawCenteredString(this.width / 2, 15, manager.localize(ResourceName.intern("info.username")), 0.35F, false);
        font.drawCenteredString(this.width / 2, 45, manager.localize(ResourceName.intern("info.password")), 0.35F, false);
    }

    private void updateButtons() {
        boolean loggedIn = false;
        this.nameField.setActive(!loggedIn);
        this.passField.setActive(!loggedIn);
        this.loginButton.setActive(!loggedIn);
        this.logoutButton.setActive(loggedIn);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("login");
    }
}
