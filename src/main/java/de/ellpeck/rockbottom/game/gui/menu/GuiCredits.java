package de.ellpeck.rockbottom.game.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.game.RockBottom;
import org.newdawn.slick.Graphics;

public class GuiCredits extends Gui{

    public GuiCredits(Gui parent){
        super(100, 100, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, -1, (int)game.getWidthInGui()-47, 2, 45, 10, game.getAssetManager().localize(RockBottom.internalRes("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        Font font = manager.getFont();
        int x = this.guiLeft+this.sizeX/2;
        int y = (int)game.getHeightInGui()-30;

        font.drawCenteredString(x, y-30, "Made by Ellpeck", 0.75F, false);
        font.drawCenteredString(x, y, "Name suggested by witsend66", 0.35F, false);
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        if(button == -1){
            game.getGuiManager().openGui(this.parent);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasGradient(){
        return false;
    }
}
