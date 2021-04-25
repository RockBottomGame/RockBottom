package de.ellpeck.rockbottom.gui.menu.account;

import de.ellpeck.rockbottom.GameAccount;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.gui.component.InputFieldComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.auth.ManagementServer;
import de.ellpeck.rockbottom.auth.ManagementServerUtil;
import de.ellpeck.rockbottom.gui.menu.MainMenuGui;

public class VerifyAccountGui extends Gui {

    private InputFieldComponent verificationField;

    private GameAccount accountToVerify;

    private boolean resentEmail;
    private int timeSinceResent;

    private boolean failedToResend;
    private ResourceName failedMsg;

    public VerifyAccountGui(Gui parent, GameAccount account) {
        super(parent);
        this.accountToVerify = account;
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        IAssetManager assetManager = game.getAssetManager();
        IGuiManager guiManager = game.getGuiManager();

        this.verificationField = new InputFieldComponent(this, this.width / 2 - 40, 60, 80, 16, true, true, false, 6, false);
        this.components.add(this.verificationField);

        this.components.add(new ButtonComponent(this, this.width / 2 - 35, 80, 70, 16, () -> {
            this.verify(game);
            return true;
        }, assetManager.localize(ResourceName.intern("button.verify"))));

        this.components.add(new ButtonComponent(this, this.width / 2 - 35, 100, 70, 16, () -> {
            if (!this.resentEmail) {
                this.resendCode(game);
                return true;
            }
            return false;
        }, assetManager.localize(ResourceName.intern("button.resend"))));

        this.components.add(new ButtonComponent(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
            guiManager.openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        IFont font = manager.getFont();
        float scale = 0.35f;
        font.drawCenteredString(this.width / 2f, 50, manager.localize(ResourceName.intern("info.server.verify_text")), scale, false);
    }

    private void verify(IGameInstance game) {
        IAssetManager assetManager = game.getAssetManager();
        ResponseGui responseGui = new ResponseGui(this);
        game.getGuiManager().openGui(responseGui);
        ManagementServerUtil.verify(ManagementServer.getServer().getApiToken(), this.verificationField.getText(),
                msg -> {
                    responseGui.displayResponse(assetManager.localize(ResourceName.intern(msg)), new MainMenuGui());
                    game.setAccount(this.accountToVerify);
                },
                msg -> responseGui.displayResponse(assetManager.localize(ResourceName.intern(msg))));
    }

    private void resendCode(IGameInstance game) {
        IAssetManager assetManager = game.getAssetManager();
        ResponseGui responseGui = new ResponseGui(this);
        game.getGuiManager().openGui(responseGui);
        ManagementServerUtil.resendCode(ManagementServer.getServer().getApiToken(),
                msg -> {
                    responseGui.displayResponse(assetManager.localize(ResourceName.intern(msg)));
                    this.resentEmail = true;
                    this.timeSinceResent = game.getTotalTicks();
                },
                msg -> responseGui.displayResponse(assetManager.localize(ResourceName.intern(msg))));
    }

    @Override
    public void update(IGameInstance game) {
        super.update(game);
        int time = game.getTotalTicks();
        if (time > this.timeSinceResent + 10 * Constants.TARGET_TPS) {
            this.resentEmail = false;
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("verify_account");
    }
}
