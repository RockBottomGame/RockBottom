package de.ellpeck.rockbottom.game.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.data.settings.Settings;
import de.ellpeck.rockbottom.game.gui.Gui;
import de.ellpeck.rockbottom.game.gui.GuiManager;
import de.ellpeck.rockbottom.game.gui.component.ComponentButton;
import de.ellpeck.rockbottom.game.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.game.gui.component.ComponentSlider;
import org.newdawn.slick.Graphics;

public class GuiSettings extends Gui{

    private ComponentInputField chatNameField;

    public GuiSettings(Gui parent){
        super(304, 100, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);
        AssetManager assetManager = game.getAssetManager();
        Settings settings = game.getSettings();

        this.components.add(new ComponentButton(this, 0, this.guiLeft, this.guiTop, 150, 16, assetManager.localize("button.controls")));
        this.components.add(new ComponentButton(this, 1, this.guiLeft+154, this.guiTop, 150, 16, assetManager.localize("button.graphics")));

        this.chatNameField = new ComponentInputField(this, this.guiLeft, this.guiTop+30, 130, 16, true, true, false, 24, true);
        this.chatNameField.setText(settings.chatName);
        this.components.add(this.chatNameField);
        this.components.add(new ComponentButton(this, 2, this.guiLeft+134, this.guiTop+30, 16, 16, "?", assetManager.localize("info.randomize_name")));

        this.components.add(new ComponentSlider(this, 3, this.guiLeft, this.guiTop+55, 150, 16, settings.autosaveIntervalSeconds, 30, 1800, new ComponentSlider.ICallback(){
            @Override
            public void onNumberChange(float mouseX, float mouseY, int min, int max, int number){
                settings.autosaveIntervalSeconds = number;
            }
        }, assetManager.localize("button.autosave_interval"), assetManager.localize("info.autosave_interval")));

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, assetManager.localize("button.back")));
    }

    @Override
    public void render(IGameInstance game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        manager.getFont().drawCenteredString(this.guiLeft+75, this.guiTop+22, manager.localize("button.chat_name"), 0.35F, false);
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        GuiManager guiManager = game.getGuiManager();

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
        else if(button == 2){
            game.getSettings().chatName = Settings.getRandomChatName();
            this.chatNameField.setText(game.getSettings().chatName);
            return true;
        }
        return false;
    }

    @Override
    public void onClosed(IGameInstance game){
        String name = this.chatNameField.getText().trim();
        if(!name.isEmpty()){
            game.getSettings().chatName = name;
        }

        game.getDataManager().savePropSettings(game.getSettings());
    }
}
