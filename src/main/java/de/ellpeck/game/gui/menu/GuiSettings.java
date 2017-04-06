package de.ellpeck.game.gui.menu;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.gui.component.ComponentButton;
import de.ellpeck.game.gui.component.ComponentSlider;
import de.ellpeck.game.gui.component.ComponentToggleButton;
import de.ellpeck.game.world.entity.player.EntityPlayer;

public class GuiSettings extends Gui{

    public GuiSettings(EntityPlayer player, Gui parent){
        super(player, 150, 100, parent);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, 0, this.guiLeft, this.guiTop, this.sizeX, 16, game.assetManager.localize("button.controls")));

        this.components.add(new ComponentToggleButton(this, 1, this.guiLeft, this.guiTop+20, this.sizeX, 16, game.settings.hardwareCursor, "button.hardware_cursor", game.assetManager.localize("info.hardware_cursor_1"), game.assetManager.localize("info.hardware_cursor_2")));
        this.components.add(new ComponentSlider(this, 1, this.guiLeft, this.guiTop+40, this.sizeX, 16, game.settings.autosaveIntervalSeconds, 10, 300, number -> game.settings.autosaveIntervalSeconds = number, game.assetManager.localize("button.autosave_interval"), game.assetManager.localize("info.autosave_interval")));

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
        }
        return false;
    }

    @Override
    public void onClosed(Game game){
        game.dataManager.saveSettings(game.settings);
    }
}
