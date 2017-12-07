package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentClickableText;
import de.ellpeck.rockbottom.api.gui.component.ComponentConfirmationPopup;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.ComponentLogin;
import de.ellpeck.rockbottom.gui.GuiPlayerEditor;

public class GuiMainMenu extends Gui{

    public static String loggedInUsername;
    public static boolean loginComplete = true; //Set this to false to display the login screen on startup

    @Override
    public void init(IGameInstance game){
        super.init(game);
        IAssetManager assetManager = game.getAssetManager();
        IGuiManager guiManager = game.getGuiManager();

        int parts = this.width/4;
        int buttonWidth = 70;
        int start = (parts-buttonWidth)/2;
        int y = this.height-30;

        this.components.add(new ComponentButton(this, start, y, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiSelectWorld(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.play"))));
        this.components.add(new ComponentButton(this, start+parts, y, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiJoinServer(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.join"))));
        this.components.add(new ComponentButton(this, start+parts*2, y, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiPlayerEditor(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.player_editor"))));
        this.components.add(new ComponentButton(this, start+parts*3, y, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiSettings(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.settings"))));

        this.components.add(new ComponentButton(this, this.width-67, 2, 65, 10, () -> {
            guiManager.openGui(new GuiCredits(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.credits"))));
        this.components.add(new ComponentButton(this, this.width-67, 14, 65, 10, () -> {
            guiManager.openGui(new GuiMods(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.mods"))));

        this.components.add(new ComponentButton(this, 2, 2, 65, 10, () -> {
            this.components.add(0, new ComponentConfirmationPopup(this, 27, 2+5, aBoolean -> {
                if(aBoolean){
                    game.exit();
                }
            }));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.quit"))));

        this.components.add(new ComponentClickableText(this, this.width-2, this.height-7, 0.25F, true, () -> Util.openWebsite("https://ellpeck.de"), "Copyright 2017 Ellpeck"));
        this.components.add(new ComponentClickableText(this, 2, this.height-7, 0.25F, false, () -> Util.openWebsite("https://rockbottom.ellpeck.de"), game.getDisplayName()+" "+game.getVersion()+" - API "+RockBottomAPI.VERSION));

        if(!loginComplete){
            this.components.add(new ComponentLogin(this, this.width-77, 35, 75));
        }
        else{
            this.components.add(new ComponentButton(this, 2, 14, 65, 10, () -> {
                this.components.add(0, new ComponentConfirmationPopup(this, 27, 19, aBoolean -> {
                    if(aBoolean){
                        loggedInUsername = null;
                        loginComplete = false;

                        this.init(game);
                    }
                }));
                return true;
            }, "Login Again"));
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g){
        super.render(game, manager, g);

        String display;
        if(loggedInUsername == null || loggedInUsername.isEmpty()){
            display = "Not logged in";
        }
        else{
            display = "Logged in as "+loggedInUsername;
        }
        manager.getFont().drawCenteredString(this.width/2, 2, display, 0.25F, false);
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
}
