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

public class GuiAccount extends Gui {

    private ComponentInputField emailField;
    private ComponentInputField usernameField;
    private ComponentInputField passField;

    private ComponentButton loginButton;
    private ComponentButton registerButton;
    private ComponentButton forgotPasswordButton;

    private ComponentButton logoutButton;

    private AccountMenu menu = AccountMenu.MAIN;
    private boolean loggedIn;
    private ResourceName feedback;

    public GuiAccount(Gui parent) {
        super(parent);
    }

    private void login(IGameInstance game) {
        Thread thread = new Thread(() -> {
            String email = this.emailField.getText();
            JsonObject obj = PostUtil.post("https://canitzp.de:38000/", new PostData("mode", "login"), new PostData("email", email), new PostData("password", this.passField.getText()));
            if (obj.has("code")) {
                int code = obj.get("code").getAsInt();
                if (code == 201) { // Login Success
                    UserAccount account = new UserAccount(UUID.fromString(obj.get("uuid").getAsString()), this.emailField.getText(), UUID.fromString(obj.get("token").getAsString()));
                    account.cache();
                    game.loginAs(account);

                    boolean firstLogin = obj.get("first_login").getAsBoolean();
                    if (firstLogin) {
                        RockBottomAPI.logger().info("Logged into account " + account.getEmail() + " for the first time!");
                    } else {
                        this.menu = AccountMenu.MAIN;
                    }
                }
            }

            synchronized (game) {
                game.getGuiManager().openGui(this);
            }

            updateFeedback(obj);
        }, ThreadHandler.ACCOUNT_SERVER);

        game.getGuiManager().openGui(new GuiInformation(this, 0.5f, false, "Logging In..."));
        thread.start();
    }

    private void register(IGameInstance game) {
        Thread thread = new Thread(() -> {
            String email = this.emailField.getText();
            String username = this.usernameField.getText();
            JsonObject obj = PostUtil.post("https://canitzp.de:38000/", new PostData("mode", "register"), new PostData("email", email), new PostData("username", username));

            synchronized (game) {
                game.getGuiManager().openGui(this);
            }
            if (obj.has("code")) {
                int code = obj.get("code").getAsInt();
                synchronized (this) {
                    if (code == 212) { // Register Success
                        setMenu(AccountMenu.MAIN, game);
                    }
                    if (code == 212 || code == 320) { // 320 = Username Taken
                        this.emailField.setText(email);
                    } else if (code == 321) { // 321 = Email Taken
                        this.usernameField.setText(username);
                    }
                }
            }

            updateFeedback(obj);
        }, ThreadHandler.ACCOUNT_SERVER);

        game.getGuiManager().openGui(new GuiInformation(this, 0.5f, false, "Creating Account..."));
        thread.start();
    }

    private void updateFeedback(JsonObject obj) {
        if (obj.has("message")) {
            synchronized (this) {
                String message = obj.get("message").getAsString();
                if (message != null) this.feedback = new ResourceName(message);
                else this.feedback = ResourceName.intern("info.account.unknown_error");
            }
        }
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        IGuiManager guiManager = game.getGuiManager();
        IAssetManager assetManager = game.getAssetManager();

        this.emailField = new ComponentInputField(this, this.width / 2 - 75, 45, 150, 16, true, true, false, 256, false);
        this.components.add(this.emailField);

        this.usernameField = new ComponentInputField(this, this.width / 2 - 75, 80, 150, 16, true, true, false, 256, false);
        this.components.add(this.usernameField);

        this.passField = new ComponentInputField(this, this.width / 2 - 75, 80, 150, 16, true, true, false, 256, false);
        this.passField.setCensored(true);
        this.components.add(this.passField);

        this.loginButton = new ComponentButton(this, 0, 0, 80, 16, () -> {
            if (this.menu == AccountMenu.MAIN) setMenu(AccountMenu.LOG_IN, game);
            else login(game);
            return true;
        }, assetManager.localize(ResourceName.intern("button.login")));
        this.components.add(this.loginButton);

        this.registerButton = new ComponentButton(this, 0, 0, 80, 16, () -> {
            if (this.menu == AccountMenu.MAIN) setMenu(AccountMenu.REGISTER, game);
            else register(game);
            return true;
        }, assetManager.localize(ResourceName.intern("button.register")));
        this.components.add(this.registerButton);

        this.forgotPasswordButton = new ComponentButton(this, 0, 0, 126, 16, () -> {
            return true;
        }, assetManager.localize(ResourceName.intern("button.forgot_password")));
        this.components.add(this.forgotPasswordButton);

        this.logoutButton = new ComponentButton(this, this.width / 2 - 75, 90, 150, 16, () -> {
            return true;
        }, assetManager.localize(ResourceName.intern("button.logout")));
        this.components.add(this.logoutButton);

        this.updateButtons(game);

        this.components.add(new ComponentButton(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
            if (this.menu != AccountMenu.MAIN) this.setMenu(AccountMenu.MAIN, game);
            else guiManager.openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        IFont font = manager.getFont();
        if (!this.loggedIn) {
            font.drawCenteredString(this.width / 2, 35, manager.localize(ResourceName.intern("info.account.email")), 0.35F, false);
            if (this.menu == AccountMenu.LOG_IN) {
                font.drawCenteredString(this.width / 2, 70, manager.localize(ResourceName.intern("info.account.password")), 0.35F, false);
            } else if (this.menu == AccountMenu.REGISTER) {
                font.drawCenteredString(this.width / 2, 70, manager.localize(ResourceName.intern("info.account.username")), 0.35F, false);
            }
        }
        if (feedback != null) {
            float textY = this.menu == AccountMenu.MAIN ? 134 : 139;
            float paddingY = 1;

            String[] split = manager.localize(feedback).split("\n");
            float lineHeight = font.getHeight(0.35F);
            float totalHeight = split.length * (lineHeight + paddingY);
            float cursorY = textY - totalHeight / 2f;
            for (String line : split) {
                font.drawCenteredString(this.width / 2, cursorY, line, 0.35F, false);
                cursorY += lineHeight + paddingY;
            }
        }
    }

    private void setMenu(AccountMenu menu, IGameInstance game) {
        this.menu = menu;
        this.feedback = null;
        updateButtons(game);
    }

    private void updateButtons(IGameInstance game) {
        this.loggedIn = game.getAccount() != null;

        this.emailField.setActive(false);
        this.usernameField.setActive(false);
        this.passField.setActive(false);
        this.loginButton.setActive(false);
        this.registerButton.setActive(false);
        this.forgotPasswordButton.setActive(false);
        this.logoutButton.setActive(false);
        switch (this.menu) {
            case MAIN:
                this.emailField.setActive(true);
                this.loginButton.setActive(true);
                this.registerButton.setActive(true);
                this.forgotPasswordButton.setActive(true);

                this.loginButton.setPos(this.width / 2 - 90, 75);
                this.registerButton.setPos(this.width / 2 + 10, 75);
                this.forgotPasswordButton.setPos(this.width / 2 - 63, 100);
                break;
            case LOG_IN:
                this.emailField.setActive(true);
                this.passField.setActive(true);
                this.loginButton.setActive(true);

                this.loginButton.setPos(this.width / 2 - 40, 110);
                break;
            case REGISTER:
                this.emailField.setActive(true);
                this.usernameField.setActive(true);
                this.registerButton.setActive(true);

                this.registerButton.setPos(this.width / 2 - 40, 110);
                break;

        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("account");
    }

    private enum AccountMenu {
        MAIN, LOG_IN, REGISTER, CHANGE_PASSWORD;
    }
}
