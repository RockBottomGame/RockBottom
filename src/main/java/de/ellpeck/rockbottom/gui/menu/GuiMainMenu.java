package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.gui.component.ComponentButton;
import org.newdawn.slick.Graphics;

public class GuiMainMenu extends Gui{

    public GuiMainMenu(){
        super(100, 100);
    }

    @Override
    public void initGui(RockBottom game){
        super.initGui(game);

        int width = (int)game.getWidthInGui();

        int parts = width/4;
        int buttonWidth = 60;
        int start = (parts-buttonWidth)/2;
        int y = (int)game.getHeightInGui()-30;

        this.components.add(new ComponentButton(this, 0, start, y, buttonWidth, 16, game.assetManager.localize("button.play")));
        this.components.add(new ComponentButton(this, 1, start+parts, y, buttonWidth, 16, game.assetManager.localize("button.join")));
        this.components.add(new ComponentButton(this, 2, start+parts*2, y, buttonWidth, 16, game.assetManager.localize("button.settings")));
        this.components.add(new ComponentButton(this, 3, start+parts*3, y, buttonWidth, 16, game.assetManager.localize("button.quit")));

        this.components.add(new ComponentButton(this, 4, width-47, 2, 45, 10, game.assetManager.localize("button.credits")));
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        manager.getFont().drawStringFromRight((float)game.getWidthInGui()-2F, (float)game.getHeightInGui()-7F, "Copyright 2017 Ellpeck", 0.25F);
    }

    @Override
    public boolean hasGradient(){
        return false;
    }

    @Override
    protected boolean tryEscape(RockBottom game){
        return false;
    }

    @Override
    public boolean onButtonActivated(RockBottom game, int button){
        if(button == 0){
            game.guiManager.openGui(new GuiSelectWorld(this));
            return true;
        }
        else if(button == 1){
            game.guiManager.openGui(new GuiJoinServer(this));
            return true;
        }
        else if(button == 2){
            game.guiManager.openGui(new GuiSettings(this));
            return true;
        }
        else if(button == 3){
            game.getContainer().exit();
            return true;
        }
        else if(button == 4){
            game.guiManager.openGui(new GuiCredits(this));
            return true;
        }
        else{
            return super.onButtonActivated(game, button);
        }
    }
}
