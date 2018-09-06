package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class ComponentBreath extends GuiComponent {

    private static final ResourceName TEX = ResourceName.intern("gui.bubble");

    public ComponentBreath(Gui gui, int x, int y, int sizeX, int sizeY) {
        super(gui, x, y, sizeX, sizeY);
    }

    public static boolean shouldDisplay(IGameInstance game) {
        AbstractEntityPlayer player = game.getPlayer();
        return player != null && (player.getBreath() < player.getMaxBreath() || !player.canBreathe);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
        if (shouldDisplay(game)) {
            ITexture tex = manager.getTexture(TEX);
            int breath = game.getPlayer().getBreath();
            int currX = this.width - 13;
            for (int i = 0; i < breath; i++) {
                tex.draw(x + currX, y, 12F, 12F);
                currX -= 13;
            }
        }
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager
            manager, IRenderer g, int x, int y) {
        if (shouldDisplay(game)) {
            if (this.isMouseOverPrioritized(game)) {
                g.drawHoverInfoAtMouse(game, manager, false, 0, manager.localize(ResourceName.intern("info.breath")) + ':', game.getPlayer().getBreath() + "/" + game.getPlayer().getMaxBreath());
            }
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("breath");
    }
}
