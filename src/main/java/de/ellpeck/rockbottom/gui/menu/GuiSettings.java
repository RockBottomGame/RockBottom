package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlider;
import org.newdawn.slick.Graphics;

public class GuiSettings extends Gui{

    public GuiSettings(Gui parent){
        super(304, 100, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);
        IAssetManager assetManager = game.getAssetManager();
        Settings settings = game.getSettings();

        this.components.add(new ComponentButton(this, 0, this.guiLeft, this.guiTop, 150, 16, assetManager.localize(RockBottom.internalRes("button.controls"))));
        this.components.add(new ComponentButton(this, 1, this.guiLeft+154, this.guiTop, 150, 16, assetManager.localize(RockBottom.internalRes("button.graphics"))));

        this.components.add(new ComponentSlider(this, 3, this.guiLeft, this.guiTop+55, 150, 16, settings.autosaveIntervalSeconds, 30, 1800, new ComponentSlider.ICallback(){
            @Override
            public void onNumberChange(float mouseX, float mouseY, int min, int max, int number){
                settings.autosaveIntervalSeconds = number;
            }
        }, assetManager.localize(RockBottom.internalRes("button.autosave_interval")), assetManager.localize(RockBottom.internalRes("info.autosave_interval"))));

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, assetManager.localize(RockBottom.internalRes("button.back"))));
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        IGuiManager guiManager = game.getGuiManager();

        if(button == -1){
            guiManager.openGui(this.parent);
            return true;
        }
        else if(button == 0){
            guiManager.openGui(new GuiKeybinds(this));
            return true;
        }
        else if(button == 1){
            guiManager.openGui(new GuiGraphics(this));
            return true;
        }
        return false;
    }

    @Override
    public void onClosed(IGameInstance game){
        super.onClosed(game);
        game.getDataManager().savePropSettings(game.getSettings());
    }
}
