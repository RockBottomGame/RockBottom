package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.List;

public class InformationGui extends Gui {

    private final String[] information;
    private final float scale;
    private final boolean canClose;

    public InformationGui(Gui parent, float scale, boolean canClose, String... information) {
        super(parent);
        this.information = information;
        this.scale = scale;
        this.canClose = canClose;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        List<String> information = manager.getFont().splitTextToLength(this.width - 10, this.scale, true, this.information);

        int middleX = this.x + this.width / 2;
        int middleY = this.y + this.height / 2;

        int height = (int) manager.getFont().getHeight(this.scale);
        int y = middleY - (information.size() * height) / 2 - 10;

        for (int i = 0; i < information.size(); i++) {
            manager.getFont().drawCenteredString(middleX, y, information.get(i), this.scale, true);
            y += height;
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("info");
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        if (this.canClose) {
            this.components.add(new ButtonComponent(this, this.width / 2 - 40, this.height - 30, 80, 16, () -> {
                game.getGuiManager().openGui(this.parent);
                return true;
            }, game.getAssetManager().localize(ResourceName.intern("button.back"))));
        }
    }

    @Override
    protected boolean tryEscape(IGameInstance game) {
        return this.canClose && super.tryEscape(game);
    }
}
