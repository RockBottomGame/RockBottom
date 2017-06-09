package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.gui.component.ComponentMessageBox;
import org.newdawn.slick.Graphics;

public class GuiMainMenu extends Gui{

    private static boolean infoBox;

    public GuiMainMenu(){
        super(100, 100);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);
        IAssetManager assetManager = game.getAssetManager();

        int width = (int)game.getWidthInGui();

        int parts = width/4;
        int buttonWidth = 60;
        int start = (parts-buttonWidth)/2;
        int y = (int)game.getHeightInGui()-30;

        this.components.add(new ComponentButton(this, 0, start, y, buttonWidth, 16, assetManager.localize(RockBottom.internalRes("button.play"))));
        this.components.add(new ComponentButton(this, 1, start+parts, y, buttonWidth, 16, assetManager.localize(RockBottom.internalRes("button.join"))));
        this.components.add(new ComponentButton(this, 2, start+parts*2, y, buttonWidth, 16, assetManager.localize(RockBottom.internalRes("button.settings"))));
        this.components.add(new ComponentButton(this, 3, start+parts*3, y, buttonWidth, 16, assetManager.localize(RockBottom.internalRes("button.quit"))));

        this.components.add(new ComponentButton(this, 4, width-47, 2, 45, 10, assetManager.localize(RockBottom.internalRes("button.credits"))));
        this.components.add(new ComponentButton(this, 5, width-47, 14, 45, 10, assetManager.localize(RockBottom.internalRes("button.mods"))));

        if(!infoBox){
            this.components.add(new ComponentMessageBox(this, 6, this.guiLeft+this.sizeX/2-75, this.guiTop+this.sizeY/2-25, 150, 50, 0.25F, FormattingCode.YELLOW+"Hello! \nThis is Rock Bottom by Ellpeck! \nYou have been given a super alpha copy of this game (that's what this is) to test out and tell me what you think about it. \nI'd really appreciate if you were to give the game a go and, while you try out everything there is to try at the moment (which is probably like 20-30 minutes of actual stuff to do), make a list of things you like, dislike or that you want added, removed or changed. Any questions or bug reports you have should also go on that list. \nWhen you're done, just give it to me in some way. \nWhile you do this, though, note that there is a lot of stuff planned for the game and that everything you see and do is subject to mild or heavy changes, however, every item you see is craftable and both copper and coal generate in the world. There might also be some easter eggs. \nAnyway, &rthanks a lot, and have fun! <3"));
            infoBox = true;
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        manager.getFont().drawStringFromRight((float)game.getWidthInGui()-2F, (float)game.getHeightInGui()-7F, "Copyright 2017 Ellpeck", 0.25F);
    }

    @Override
    public boolean hasGradient(){
        return false;
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
            game.getContainer().exit();
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
            this.components.remove(6);
            return true;
        }
        else{
            return super.onButtonActivated(game, button);
        }
    }
}
