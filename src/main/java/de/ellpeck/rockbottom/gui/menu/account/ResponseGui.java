package de.ellpeck.rockbottom.gui.menu.account;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.List;

public class ResponseGui extends Gui {

    private final int openTime;
    private ButtonComponent cancelButton;

    private boolean arrived;
    private Gui redirect;
    private String responseMessage;

    private ButtonComponent backButton;

    public ResponseGui(Gui parent) {
        super(0, 0, parent);
        this.openTime = RockBottomAPI.getGame().getTotalTicks();
        this.redirect = parent;
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        IAssetManager assetManager = game.getAssetManager();

        this.cancelButton = new ButtonComponent(this, this.width / 2 - 30, this.height / 2 + 20, 60, 16, () -> {
            // TODO Cancel the call
            game.getGuiManager().openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.cancel")));
        this.cancelButton.setActive(!this.arrived && this.waitedTooLong());
        this.components.add(this.cancelButton);

        this.backButton = new ButtonComponent(this, this.width / 2 - 30, this.height / 2 + 20, 60, 16, () -> {
            game.getGuiManager().openGui(this.redirect);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back")));
        this.backButton.setActive(this.arrived);
        this.components.add(this.backButton);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        IFont font = manager.getFont();
        float fontScale = 0.35F;
        if (!arrived) {
            font.drawCenteredString(this.width / 2, this.height / 2 - 20, manager.localize(ResourceName.intern("info.server.waiting_response")), fontScale, false);
            // TODO Draw the waiting symbol
        } else {
            List<String> msgs = font.splitTextToLength(256, fontScale, false, this.responseMessage);
            for (int i = 0; i < msgs.size(); i++) {
                String msg = msgs.get(i);
                font.drawCenteredString(this.width / 2, this.height / 2 - 20 + i * font.getHeight(fontScale), msg, fontScale, false);
            }
        }
    }

    @Override
    public void update(IGameInstance game) {
        super.update(game);

        if (!this.arrived && !this.cancelButton.isActive() && this.waitedTooLong()) {
            this.cancelButton.setActive(true);
        }
    }

    private boolean waitedTooLong() {
        int time = RockBottomAPI.getGame().getTotalTicks();
        return (time - this.openTime) > 10 * Constants.TARGET_TPS;
    }

    public void displayResponse(String msg, Gui redirect) {
        this.arrived = true;
        this.redirect = redirect;
        this.responseMessage = msg;
        this.backButton.setActive(true);
        this.cancelButton.setActive(false);

        RockBottomAPI.logger().info(msg);
    }

    public void displayResponse(String msg) {
        this.displayResponse(msg, this.parent);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("server_response");
    }
}
