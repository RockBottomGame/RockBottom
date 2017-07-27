package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.GuiPlayerEditor;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.newdawn.slick.Graphics;

public class GuiMainMenu extends Gui{

    public GuiMainMenu(){
        super(100, 100);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);
        IAssetManager assetManager = game.getAssetManager();

        int width = (int)game.getWidthInGui();

        int parts = width/4;
        int buttonWidth = 70;
        int start = (parts-buttonWidth)/2;
        int y = (int)game.getHeightInGui()-30;

        this.components.add(new ComponentButton(this, 0, start, y, buttonWidth, 16, assetManager.localize(AbstractGame.internalRes("button.play"))));
        this.components.add(new ComponentButton(this, 1, start+parts, y, buttonWidth, 16, assetManager.localize(AbstractGame.internalRes("button.join"))));
        this.components.add(new ComponentButton(this, 6, start+parts*2, y, buttonWidth, 16, assetManager.localize(AbstractGame.internalRes("button.player_editor"))));
        this.components.add(new ComponentButton(this, 2, start+parts*3, y, buttonWidth, 16, assetManager.localize(AbstractGame.internalRes("button.settings"))));

        this.components.add(new ComponentButton(this, 4, width-47, 2, 45, 10, assetManager.localize(AbstractGame.internalRes("button.credits"))));
        this.components.add(new ComponentButton(this, 5, width-47, 14, 45, 10, assetManager.localize(AbstractGame.internalRes("button.mods"))));

        this.components.add(new ComponentButton(this, 3, 2, 2, 45, 10, assetManager.localize(AbstractGame.internalRes("button.quit"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        float height = (float)game.getHeightInGui();
        Font font = manager.getFont();

        font.drawStringFromRight((float)game.getWidthInGui()-2F, height-7F, "Copyright 2017 Ellpeck", 0.25F);
        font.drawString(2, height-7F, game.getDisplayName()+" "+game.getVersion()+" - API "+RockBottomAPI.VERSION, 0.25F);
    }

    @Override
    public boolean hasGradient(){
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("main_menu");
    }

    @Override
    protected boolean tryEscape(IGameInstance game){
        return false;
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        IGuiManager guiManager = game.getGuiManager();

        if(button == 0){
            guiManager.openGui(new GuiSelectWorld(this));
            return true;
        }
        else if(button == 1){
            guiManager.openGui(new GuiJoinServer(this));
            return true;
        }
        else if(button == 2){
            guiManager.openGui(new GuiSettings(this));
            return true;
        }
        else if(button == 3){
            game.exit();
            return true;
        }
        else if(button == 4){
            guiManager.openGui(new GuiCredits(this));
            return true;
        }
        else if(button == 5){
            guiManager.openGui(new GuiMods(this));
            return true;
        }
        else if(button == 6){
            guiManager.openGui(new GuiPlayerEditor(this));
            return true;
        }
        else{
            return super.onButtonActivated(game, button);
        }
    }
}
