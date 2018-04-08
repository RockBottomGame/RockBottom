package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.Constants;
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
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.GuiPlayerEditor;
import de.ellpeck.rockbottom.util.ChangelogManager;
import de.ellpeck.rockbottom.util.ChangelogManager.Changelog;

public class GuiMainMenu extends Gui{

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

        ComponentButton modsButton = new ComponentButton(this, start, y-54, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiMods(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.mods")));
        this.components.add(modsButton);
        modsButton.setActive(false);

        ComponentButton contentButton = new ComponentButton(this, start, y-37, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiContentPacks(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.content_packs")));
        this.components.add(contentButton);
        contentButton.setActive(false);

        this.components.add(new ComponentButton(this, start, y-20, buttonWidth, 16, () -> {
            modsButton.setActive(!modsButton.isActive());
            contentButton.setActive(!contentButton.isActive());
            return true;
        }, "Game Content"));
        this.components.add(new ComponentButton(this, start, y, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiChangelog(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.changelog"))){
            @Override
            protected String getText(){
                Changelog log = ChangelogManager.getChangelog();
                if(log != null){
                    if(log.isStableNewer){
                        return FormattingCode.ORANGE+super.getText();
                    }
                    else if(log.isLatestNewer){
                        return FormattingCode.YELLOW+super.getText();
                    }
                }
                return super.getText();
            }
        });

        this.components.add(new ComponentButton(this, start+partWidth-5, y-20, buttonWidth+10, 16, () -> {
            guiManager.openGui(new GuiSelectWorld(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.play"))));
        this.components.add(new ComponentButton(this, start+partWidth-5, y, buttonWidth+10, 16, () -> {
            guiManager.openGui(new GuiJoinServer(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.join"))));

        ComponentButton editorButton = new ComponentButton(this, start+partWidth*2, y-37, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiPlayerEditor(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.player_editor")));
        this.components.add(editorButton);
        editorButton.setActive(false);

        this.components.add(new ComponentButton(this, start+partWidth*2, y-20, buttonWidth, 16, ()->{
            editorButton.setActive(!editorButton.isActive());
            return true;
        }, "The Player"));
        this.components.add(new ComponentButton(this, start+partWidth*2, y, buttonWidth, 16, () -> {
            guiManager.openGui(new GuiSettings(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.settings"))));

        this.components.add(new ComponentButton(this, this.width-52, 2, 50, 10, () -> {
            guiManager.openGui(new GuiCredits(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.credits"))));

        this.components.add(new ComponentButton(this, 2, 2, 50, 10, () -> {
            this.components.add(new ComponentConfirmationPopup(this, 27, 2+5, aBoolean -> {
                if(aBoolean){
                    game.exit();
                }
            }));
            this.sortComponents();
            return true;
        }, assetManager.localize(ResourceName.intern("button.quit"))));

        this.components.add(new ComponentClickableText(this, this.width-2, this.height-7, 0.25F, true, () -> Util.openWebsite(Constants.ELLPECK_LINK), "Copyright 2017-2018 Ellpeck"));
        this.components.add(new ComponentClickableText(this, 2, this.height-7, 0.25F, false, () -> Util.openWebsite(Constants.WEBSITE_LINK), game.getDisplayName()+' '+game.getVersion()+" - API "+RockBottomAPI.VERSION));
    }

    @Override
    public boolean hasGradient(){
        return false;
    }

    @Override
    public ResourceName getName(){
        return ResourceName.intern("main_menu");
    }

    @Override
    protected boolean tryEscape(IGameInstance game){
        return false;
    }
}
