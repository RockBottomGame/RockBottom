package de.ellpeck.rockbottom.gui.menu;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.GuiInformation;
import de.ellpeck.rockbottom.net.login.PostData;
import de.ellpeck.rockbottom.net.login.PostUtil;
import de.ellpeck.rockbottom.net.login.UserAccount;

import java.util.UUID;

public class GuiLogin extends Gui {

    private ComponentInputField nameField;
    private ComponentInputField passField;
    private ComponentButton loginButton;
    private ComponentButton logoutButton;

    public GuiLogin(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        IGuiManager guiManager = game.getGuiManager();
        IAssetManager assetManager = game.getAssetManager();

        this.nameField = new ComponentInputField(this, this.width / 2 - 75, 25, 150, 16, true, true, false, 256, false);
        this.components.add(this.nameField);

        this.passField = new ComponentInputField(this, this.width / 2 - 75, 55, 150, 16, true, true, false, 256, false);
        this.passField.setCensored(true);
        this.components.add(this.passField);

        this.loginButton = new ComponentButton(this, this.width / 2 - 50, 80, 100, 16, () -> {
            Thread thread = new Thread(() -> {
                UserAccount account = UserAccount.login(game, this.nameField.getText(), this.passField.getText());
                if (account != null) {
                    account.renew();
                }
                game.getGuiManager().openGui(this);
            });

            thread.start();
            game.getGuiManager().openGui(new GuiInformation(this, 0.5f, false, "Logging In..."));

            return true;
        }, assetManager.localize(ResourceName.intern("button.login")));
        this.components.add(this.loginButton);

        this.logoutButton = new ComponentButton(this, this.width / 2 - 50, 80, 100, 16, () -> {
            return true;
        }, assetManager.localize(ResourceName.intern("button.logout")));
        this.components.add(this.logoutButton);

        this.updateButtons();

        this.components.add(new ComponentButton(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
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
