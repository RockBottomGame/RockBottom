package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentClickableText;
import de.ellpeck.rockbottom.api.gui.component.ComponentConfirmationPopup;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.GuiPlayerEditor;

import java.util.logging.Level;

public class GuiMainMenu extends Gui{

    public GuiMainMenu(){
        if(GuiChangelog.changelog == null && !GuiChangelog.changelogGrabError){
            Thread loaderThread = new Thread(() -> {
                try{
                    GuiChangelog.loadChangelog();
                }
                catch(Exception e){
                    RockBottomAPI.logger().log(Level.WARNING, "There was an error trying to grab and parse the changelog", e);
                    GuiChangelog.changelogGrabError = true;
                }
            }, "ChangelogGrabber");
            loaderThread.setDaemon(true);
            loaderThread.start();
        }
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);
        IAssetManager assetManager = game.getAssetManager();
        IGuiManager guiManager = game.getGuiManager();

        int buttonAmount = 3;
        int partWidth = this.width/buttonAmount;
        int buttonWidth = 75;
        int start = (this.width-buttonWidth*buttonAmount-(buttonAmount-1)*(partWidth-buttonWidth))/2;
        int y = this.height-30;

        this.components.add(new ComponentButton(this, start, y-20, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiPlayerEditor(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.player_editor"))));
        this.components.add(new ComponentButton(this, start, y, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiMods(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.mods"))));

        this.components.add(new ComponentButton(this, start+partWidth-5, y-20, buttonWidth+10, 16, () -> {
            guiManager.openGui(new GuiSelectWorld(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.play"))));
        this.components.add(new ComponentButton(this, start+partWidth-5, y, buttonWidth+10, 16, () -> {
            guiManager.openGui(new GuiJoinServer(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.join"))));

        this.components.add(new ComponentButton(this, start+partWidth*2, y-20, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiSettings(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.settings"))));
        this.components.add(new ComponentButton(this, start+partWidth*2, y, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiChangelog(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.changelog"))){
            @Override
            protected String getText(){
                if(GuiChangelog.changelog != null){
                    if(GuiChangelog.changelog.isStableNewer){
                        return FormattingCode.ORANGE+super.getText();
                    }
                    else if(GuiChangelog.changelog.isLatestNewer){
                        return FormattingCode.YELLOW+super.getText();
                    }
                }
                return super.getText();
            }
        });

        this.components.add(new ComponentButton(this, this.width-52, 2, 50, 10, () -> {
            guiManager.openGui(new GuiCredits(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.credits"))));

        this.components.add(new ComponentButton(this, 2, 2, 50, 10, () -> {
            this.components.add(0, new ComponentConfirmationPopup(this, 27, 2+5, aBoolean -> {
                if(aBoolean){
                    game.exit();
                }
            }));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.quit"))));

        this.components.add(new ComponentClickableText(this, this.width-2, this.height-7, 0.25F, true, () -> Util.openWebsite("https://ellpeck.de"), "Copyright 2017 Ellpeck"));
        this.components.add(new ComponentClickableText(this, 2, this.height-7, 0.25F, false, () -> Util.openWebsite("https://rockbottom.ellpeck.de"), game.getDisplayName()+" "+game.getVersion()+" - API "+RockBottomAPI.VERSION));
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
