package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class SkillComponent extends GuiComponent {

    public SkillComponent(Gui gui, int x, int y, int width, int height) {
        super(gui, x, y, width, height);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
        AbstractPlayerEntity player = game.getPlayer();
        int points = player.getSkillPoints();
        float percentage = player.getSkillPercentage();

        g.addFilledRect(x, y + this.height - 5, this.width, 5, Colors.multiply(Colors.GREEN, 0.5F, 0.5F, 0.5F, 0.35F));
        g.addFilledRect(x, y + this.height - 5, this.width * percentage, 5, Colors.GREEN);
        g.addEmptyRect(x, y + this.height - 5, this.width, 5, Colors.BLACK);

        manager.getFont().drawString(x, y, String.valueOf(points), 0.5F);
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
        if (this.isMouseOverPrioritized(game)) {
            AbstractPlayerEntity player = game.getPlayer();
            g.drawHoverInfoAtMouse(game, manager, false, 0, manager.localize(ResourceName.intern("info.skill.points"), player.getSkillPoints()), manager.localize(ResourceName.intern("info.skill.percentage"), Util.ceil(100 - player.getSkillPercentage() * 100F)));
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("skill");
    }
}
