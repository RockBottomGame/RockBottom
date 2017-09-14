package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentConfirmationPopup;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.GuiPlayerEditor;

public class GuiMainMenu extends Gui{

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);
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

        this.components.add(new ComponentButton(this, this.width-47, 2, 45, 10, () -> {
            guiManager.openGui(new GuiCredits(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.credits"))));
        this.components.add(new ComponentButton(this, this.width-47, 14, 45, 10, () -> {
            guiManager.openGui(new GuiMods(this));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.mods"))));

        this.components.add(new ComponentButton(this, 2, 2, 45, 10, () -> {
            this.components.add(0, new ComponentConfirmationPopup(this, 27, 2+5, aBoolean -> {
                if(aBoolean){
                    game.exit();
                }
            }));
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.quit"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g){
        super.render(game, manager, g);

        Font font = manager.getFont();

        font.drawStringFromRight(this.width-2F, this.height-7F, "Copyright 2017 Ellpeck", 0.25F);
        font.drawString(2, this.height-7F, game.getDisplayName()+" "+game.getVersion()+" - API "+RockBottomAPI.VERSION, 0.25F);
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
