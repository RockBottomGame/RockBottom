package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.data.settings.Settings;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.gui.component.ComponentButton;
import de.ellpeck.rockbottom.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.gui.component.ComponentSlider;
import de.ellpeck.rockbottom.gui.component.ComponentSlider.ICallback;
import de.ellpeck.rockbottom.gui.component.ComponentToggleButton;
import org.newdawn.slick.Graphics;

public class GuiSettings extends Gui{

    private ComponentInputField chatNameField;

    public GuiSettings(Gui parent){
        super(304, 100, parent);
    }

    @Override
    public void initGui(RockBottom game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, 0, this.guiLeft, this.guiTop, 150, 16, game.assetManager.localize("button.controls")));
        this.components.add(new ComponentButton(this, 1, this.guiLeft+154, this.guiTop, 150, 16, game.assetManager.localize("button.graphics")));

        this.chatNameField = new ComponentInputField(this, this.guiLeft, this.guiTop+30, 130, 16, true, true, false, 24, true);
        this.chatNameField.setText(game.settings.chatName);
        this.components.add(this.chatNameField);
        this.components.add(new ComponentButton(this, 2, this.guiLeft+134, this.guiTop+30, 16, 16, "?", game.assetManager.localize("info.randomize_name")));

        this.components.add(new ComponentSlider(this, 3, this.guiLeft, this.guiTop+55, 150, 16, game.settings.autosaveIntervalSeconds, 30, 1800, new ICallback(){
            @Override
            public void onNumberChange(float mouseX, float mouseY, int min, int max, int number){
                game.settings.autosaveIntervalSeconds = number;
            }
        }, game.assetManager.localize("button.autosave_interval"), game.assetManager.localize("info.autosave_interval")));

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, game.assetManager.localize("button.back")));
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        manager.getFont().drawCenteredString(this.guiLeft+75, this.guiTop+22, manager.localize("button.chat_name"), 0.35F, false);
    }

    @Override
    public boolean onButtonActivated(RockBottom game, int button){
        if(button == -1){
            game.guiManager.openGui(this.parent);
            return true;
        }
        else if(button == 0){
            game.guiManager.openGui(new GuiKeybinds(this));
            return true;
        }
        else if(button == 1){
            game.guiManager.openGui(new GuiGraphics(this));
            return true;
        }
        else if(button == 2){
            game.settings.chatName = Settings.getRandomChatName();
            this.chatNameField.setText(game.settings.chatName);
            return true;
        }
        return false;
    }

    @Override
    public void onClosed(RockBottom game){
        String name = this.chatNameField.getText().trim();
        if(!name.isEmpty()){
            game.settings.chatName = name;
        }

        game.dataManager.savePropSettings(game.settings);
    }
}
