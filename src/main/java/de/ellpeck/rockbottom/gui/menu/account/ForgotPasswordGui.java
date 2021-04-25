package de.ellpeck.rockbottom.gui.menu.account;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.gui.component.InputFieldComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.auth.ManagementServerUtil;

public class ForgotPasswordGui extends Gui {

    private InputFieldComponent emailField;

    public ForgotPasswordGui(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        IGuiManager guiManager = game.getGuiManager();
        IAssetManager assetManager = game.getAssetManager();

        this.emailField = new InputFieldComponent(this, this.width / 2 - 75, 60, 150, 16, true, true, false, 256, false);
        this.components.add(this.emailField);

        this.components.add(new ButtonComponent(this, this.width / 2 - 30, 90, 60, 16, () -> {
            this.requestPasswordReset(game);
            return true;
        }, assetManager.localize(ResourceName.intern("button.send"))));

        this.components.add(new ButtonComponent(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
            guiManager.openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        IFont font = manager.getFont();
        font.drawCenteredString(this.width / 2f, 50, manager.localize(ResourceName.intern("info.email")), 0.35F, false);
    }

    private void requestPasswordReset(IGameInstance game) {
        IAssetManager assetManager = game.getAssetManager();
        ResponseGui responseGui = new ResponseGui(this);
        game.getGuiManager().openGui(responseGui);
        ManagementServerUtil.requestPasswordReset(this.emailField.getText(),
                msg -> {
                    RockBottomAPI.logger().info(assetManager.localize(ResourceName.intern(msg)));
                    game.getGuiManager().openGui(new ResetPasswordGui(this.parent, this.emailField.getText()));
                },
                msg -> {
                    responseGui.displayResponse(assetManager.localize(ResourceName.intern(msg)));
                }
        );
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("forgot_password");
    }
}
