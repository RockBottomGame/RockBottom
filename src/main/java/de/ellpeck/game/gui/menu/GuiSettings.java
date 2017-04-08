package de.ellpeck.game.gui.menu;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.gui.component.ComponentButton;
import de.ellpeck.game.gui.component.ComponentSlider;
import de.ellpeck.game.gui.component.ComponentSlider.ICallback;
import de.ellpeck.game.gui.component.ComponentToggleButton;
import de.ellpeck.game.world.entity.player.EntityPlayer;

public class GuiSettings extends Gui{

    public GuiSettings(EntityPlayer player, Gui parent){
        super(player, 304, 100, parent);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, 0, this.guiLeft+this.sizeX/2-75, this.guiTop, 150, 16, game.assetManager.localize("button.controls")));

        this.components.add(new ComponentToggleButton(this, 1, this.guiLeft, this.guiTop+20, 150, 16, game.settings.hardwareCursor, "button.hardware_cursor", game.assetManager.localize("info.hardware_cursor")));
        this.components.add(new ComponentSlider(this, 2, this.guiLeft+154, this.guiTop+20, 150, 16, (int)game.settings.cursorScale, 1, 16, new ICallback(){
            @Override
            public void onLetGo(float mouseX, float mouseY, int number){
                game.settings.cursorScale = number;
                game.assetManager.reloadCursor(game);
            }
        }, game.assetManager.localize("button.cursor_scale")));

        this.components.add(new ComponentSlider(this, 3, this.guiLeft, this.guiTop+40, 150, 16, game.settings.guiScale, 1, 8, new ICallback(){
            @Override
            public void onLetGo(float mouseX, float mouseY, int number){
                game.settings.guiScale = number;
                game.guiManager.shouldReInit = true;
            }
        }, game.assetManager.localize("button.gui_scale")));
        this.components.add(new ComponentSlider(this, 4, this.guiLeft+154, this.guiTop+40, 150, 16, game.settings.renderScale, 1, 128, new ICallback(){
            @Override
            public void onLetGo(float mouseX, float mouseY, int number){
                game.settings.renderScale = number;
            }
        }, game.assetManager.localize("button.render_scale")));

        this.components.add(new ComponentSlider(this, 5, this.guiLeft, this.guiTop+60, 150, 16, game.settings.autosaveIntervalSeconds, 10, 300, new ICallback(){
            @Override
            public void onNumberChange(float mouseX, float mouseY, int number){
                game.settings.autosaveIntervalSeconds = number;
            }
        }, game.assetManager.localize("button.autosave_interval"), game.assetManager.localize("info.autosave_interval")));

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, game.assetManager.localize("button.back")));
    }

    @Override
    public boolean onButtonActivated(Game game, int button){
        if(button == -1){
            game.guiManager.openGui(this.parent);
            return true;
        }
        else if(button == 0){
            game.guiManager.openGui(new GuiKeybinds(this.player, this));
        }
        else if(button == 1){
            game.settings.hardwareCursor = !game.settings.hardwareCursor;
            game.assetManager.reloadCursor(game);
        }
        return false;
    }

    @Override
    public void onClosed(Game game){
        game.dataManager.saveSettings(game.settings);
    }
}
