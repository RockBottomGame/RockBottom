package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlider;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;

public class GuiSettings extends Gui{

    public GuiSettings(Gui parent){
        super(304, 150, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);
        IAssetManager assetManager = game.getAssetManager();
        Settings settings = game.getSettings();
        IGuiManager guiManager = game.getGuiManager();

        this.components.add(new ComponentButton(this, this.guiLeft+this.sizeX/2-75, this.guiTop, 150, 16, () -> {
            guiManager.openGui(new GuiKeybinds(this));
            return true;
        }, assetManager.localize(AbstractGame.internalRes("button.controls"))));
        this.components.add(new ComponentButton(this, this.guiLeft+this.sizeX/2-75, this.guiTop+20, 150, 16, () -> {
            guiManager.openGui(new GuiGraphics(this));
            return true;
        }, assetManager.localize(AbstractGame.internalRes("button.graphics"))));
        this.components.add(new ComponentButton(this, this.guiLeft+this.sizeX/2-75, this.guiTop+40, 150, 16, () -> {
            guiManager.openGui(new GuiLanguage(this));
            return true;
        }, assetManager.localize(AbstractGame.internalRes("button.language"))));

        this.components.add(new ComponentSlider(this, this.guiLeft+this.sizeX/2-75, this.guiTop+65, 150, 16, settings.autosaveIntervalSeconds, 30, 1800, ((integer, aBoolean) -> settings.autosaveIntervalSeconds = integer), assetManager.localize(AbstractGame.internalRes("button.autosave_interval")), assetManager.localize(AbstractGame.internalRes("info.autosave_interval"))));

        this.components.add(new ComponentButton(this, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, () -> {
            guiManager.openGui(this.parent);
            return true;
        }, assetManager.localize(AbstractGame.internalRes("button.back"))));
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("settings");
    }

    @Override
    public void onClosed(IGameInstance game){
        super.onClosed(game);
        game.getDataManager().savePropSettings(game.getSettings());
    }
}
