package de.ellpeck.game.gui.menu;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.gui.component.ComponentButton;
import org.newdawn.slick.Graphics;

public class GuiMainMenu extends Gui{

    public GuiMainMenu(){
        super(100, 100);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        int parts = (int)game.getWidthInGui()/3;
        int buttonWidth = 90;
        int start = (parts-buttonWidth)/2;
        int y = (int)game.getHeightInGui()-30;

        this.components.add(new ComponentButton(this, 0, start, y, buttonWidth, 16, game.assetManager.localize("button.play")));
        this.components.add(new ComponentButton(this, 1, start+parts, y, buttonWidth, 16, game.assetManager.localize("button.settings")));
        this.components.add(new ComponentButton(this, 2, start+parts*2, y, buttonWidth, 16, game.assetManager.localize("button.quit")));
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        manager.getFont().drawStringFromRight((float)game.getWidthInGui()-2F, (float)game.getHeightInGui()-7F, "Copyright 2017 Ellpeck", 0.25F);
    }

    @Override
    public boolean hasGradient(){
        return false;
    }

    @Override
    protected boolean tryEscape(Game game){
        return false;
    }

    @Override
    public boolean onButtonActivated(Game game, int button){
        if(button == 0){
            game.guiManager.openGui(new GuiSelectWorld(this));
            return true;
        }
        else if(button == 1){
            game.guiManager.openGui(new GuiSettings(this));
            return true;
        }
        else if(button == 2){
            game.getContainer().exit();
            return true;
        }
        else{
            return super.onButtonActivated(game, button);
        }
    }
}
