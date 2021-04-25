package de.ellpeck.rockbottom.gui.menu.account;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.gui.component.InputFieldComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.auth.ManagementServerUtil;

public class ResetPasswordGui extends Gui {

    private String email;

    private InputFieldComponent emailField;
    private InputFieldComponent codeField;
    private InputFieldComponent passField;
    private InputFieldComponent repeatPassField;

    public ResetPasswordGui(Gui parent, String email) {
        super(parent);
        this.email = email;
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.emailField = new InputFieldComponent(this, this.width / 2 - 75, 25, 150, 16, true, false, false, 256, false);
        this.emailField.setText(this.email);
        this.components.add(this.emailField);

        this.passField = new InputFieldComponent(this, this.width / 2 - 75, 55, 150, 16, true, true, false, 256, false);
        this.passField.setCensored(true);
        this.components.add(this.passField);

        this.repeatPassField = new InputFieldComponent(this, this.width / 2 - 75, 85, 150, 16, true, true, false, 256, false);
        this.repeatPassField.setCensored(true);
        this.components.add(this.repeatPassField);

        this.codeField = new InputFieldComponent(this, this.width / 2 - 65, 115, 60, 16, true, true, false, 6, false);
        this.components.add(this.codeField);

        this.components.add(new ButtonComponent(this, this.width / 2 + 5, 115, 60, 16, () -> {
            this.resetPassword(game);
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.reset"))));

        this.components.add(new ButtonComponent(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        IFont font = manager.getFont();
        float scale = 0.35F;
        font.drawCenteredString(this.width / 2f, 15, manager.localize(ResourceName.intern("info.email")), scale, false);
        font.drawCenteredString(this.width / 2f, 45, manager.localize(ResourceName.intern("info.new_password")), scale, false);
        font.drawCenteredString(this.width / 2f, 75, manager.localize(ResourceName.intern("info.repeat_password")), scale, false);
        font.drawCenteredString(this.width / 2f - 65 + 30, 105, manager.localize(ResourceName.intern("info.server.verify_text")), scale, false);
    }

    private void resetPassword(IGameInstance game) {
        ResponseGui responseGui = new ResponseGui(this);
        game.getGuiManager().openGui(responseGui);
        if (!this.passField.getText().equals(this.repeatPassField.getText())) {
            responseGui.displayResponse(game.getAssetManager().localize(ResourceName.intern("info.server.password_mismatch")));
        } else {
            ManagementServerUtil.resetPassword(this.email, this.codeField.getText(), this.passField.getText(),
                    msg -> responseGui.displayResponse(game.getAssetManager().localize(ResourceName.intern(msg)), this.parent),
                    msg -> responseGui.displayResponse(game.getAssetManager().localize(ResourceName.intern(msg))));
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("reset_password");
    }
}
