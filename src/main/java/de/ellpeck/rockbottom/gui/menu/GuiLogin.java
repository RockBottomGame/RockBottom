package de.ellpeck.rockbottom.gui.menu;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
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
import de.ellpeck.rockbottom.util.thread.ThreadHandler;

import java.util.UUID;

public class GuiLogin extends Gui {

    private ComponentInputField emailField;
    private ComponentInputField passField;
    private ComponentButton loginButton;
    private ComponentButton logoutButton;

    private boolean loggedIn;
    private ResourceName feedback;

    public GuiLogin(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        IGuiManager guiManager = game.getGuiManager();
        IAssetManager assetManager = game.getAssetManager();


        this.emailField = new ComponentInputField(this, this.width / 2 - 75, 25, 150, 16, true, true, false, 256, false);
        this.components.add(this.emailField);

        this.passField = new ComponentInputField(this, this.width / 2 - 75, 55, 150, 16, true, true, false, 256, false);
        this.passField.setCensored(true);
        this.components.add(this.passField);

        this.loginButton = new ComponentButton(this, this.width / 2 - 50, 80, 100, 16, () -> {
            Thread thread = new Thread(() -> {
                JsonObject obj = PostUtil.post("https://canitzp.de:38000/login", new PostData("mode", "login"), new PostData("email", this.emailField.getText()), new PostData("password", this.passField.getText()));
                if (obj.has("code")) {
                    int code = obj.get("code").getAsInt();
                    if (code == 100) {
                        UserAccount account = new UserAccount(UUID.fromString(obj.get("uuid").getAsString()), this.emailField.getText(), UUID.fromString(obj.get("token").getAsString()));
                        account.cache();
                        game.loginAs(account);

                        boolean firstLogin = obj.get("was_verified").getAsBoolean();
                        if (firstLogin) {
                            RockBottomAPI.logger().info("Logged into account " + account.getUsername() + " for the first time!");
                        }
                    }
                }

                synchronized (game) {
                    game.getGuiManager().openGui(this);
                }

                synchronized (this) {
                    if (obj.has("message")) {
                        String message = obj.get("message").getAsString();
                        if (message != null) this.feedback = new ResourceName(message);
                        else this.feedback = ResourceName.intern("info.account.unknown_error");
                    }
                }
            }, ThreadHandler.ACCOUNT_SERVER);

            game.getGuiManager().openGui(new GuiInformation(this, 0.5f, false, "Logging In..."));
            thread.start();

            return true;
        }, assetManager.localize(ResourceName.intern("button.login")));
        this.components.add(this.loginButton);

        this.logoutButton = new ComponentButton(this, this.width / 2 - 50, 80, 100, 16, () -> {
            return true;
        }, assetManager.localize(ResourceName.intern("button.logout")));
        this.components.add(this.logoutButton);

        this.updateButtons(game);

        this.components.add(new ComponentButton(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
            guiManager.openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        IFont font = manager.getFont();
        if (!this.loggedIn) {
            font.drawCenteredString(this.width / 2, 15, manager.localize(ResourceName.intern("info.account.email")), 0.35F, false);
            font.drawCenteredString(this.width / 2, 45, manager.localize(ResourceName.intern("info.account.password")), 0.35F, false);
        }
        if (feedback != null) {
            font.drawCenteredString(this.width / 2, 120, manager.localize(feedback), 0.35F, false);
        }
    }

    private void updateButtons(IGameInstance game) {
        this.loggedIn = game.getAccount() != null;

        this.emailField.setActive(!this.loggedIn);
        this.passField.setActive(!this.loggedIn);
        this.loginButton.setActive(!this.loggedIn);
        this.logoutButton.setActive(this.loggedIn);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("login");
    }
}
