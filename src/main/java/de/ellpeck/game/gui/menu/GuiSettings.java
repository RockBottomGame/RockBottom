package de.ellpeck.game.gui.menu;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.gui.component.ComponentButton;
import de.ellpeck.game.gui.component.ComponentInputField;
import de.ellpeck.game.gui.component.ComponentSlider;
import de.ellpeck.game.gui.component.ComponentSlider.ICallback;
import de.ellpeck.game.gui.component.ComponentToggleButton;
import org.newdawn.slick.Graphics;

public class GuiSettings extends Gui{

    private ComponentInputField chatNameField;

    public GuiSettings(Gui parent){
        super(304, 130, parent);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, 0, this.guiLeft+this.sizeX/2-75, this.guiTop, 150, 16, game.assetManager.localize("button.controls")));

        this.components.add(new ComponentToggleButton(this, 1, this.guiLeft, this.guiTop+20, 150, 16, game.settings.hardwareCursor, "button.hardware_cursor", game.assetManager.localize("info.hardware_cursor")));
        this.components.add(new ComponentSlider(this, 2, this.guiLeft+154, this.guiTop+20, 150, 16, (int)game.settings.cursorScale, 1, 16, new ICallback(){
            @Override
            public void onLetGo(float mouseX, float mouseY, int min, int max, int number){
                game.settings.cursorScale = number;
                game.assetManager.reloadCursor(game);
            }
        }, game.assetManager.localize("button.cursor_scale")));

        this.components.add(new ComponentSlider(this, 3, this.guiLeft, this.guiTop+40, 150, 16, game.settings.guiScale, 1, 8, new ICallback(){
            @Override
            public void onLetGo(float mouseX, float mouseY, int min, int max, int number){
                game.settings.guiScale = number;
                game.guiManager.shouldReInit = true;
            }
        }, game.assetManager.localize("button.gui_scale")));
        this.components.add(new ComponentSlider(this, 4, this.guiLeft+154, this.guiTop+40, 150, 16, game.settings.renderScale, 1, 128, new ICallback(){
            @Override
            public void onLetGo(float mouseX, float mouseY, int min, int max, int number){
                game.settings.renderScale = number;
            }
        }, game.assetManager.localize("button.render_scale")));

        this.components.add(new ComponentSlider(this, 5, this.guiLeft, this.guiTop+60, 150, 16, game.settings.autosaveIntervalSeconds, 30, 1800, new ICallback(){
            @Override
            public void onNumberChange(float mouseX, float mouseY, int min, int max, int number){
                game.settings.autosaveIntervalSeconds = number;
            }
        }, game.assetManager.localize("button.autosave_interval"), game.assetManager.localize("info.autosave_interval")));
        this.components.add(new ComponentSlider(this, 6, this.guiLeft+154, this.guiTop+60, 150, 16, game.settings.targetFps, 30, 256, new ICallback(){
            @Override
            public void onLetGo(float mouseX, float mouseY, int min, int max, int number){
                game.settings.targetFps = number;
                game.getContainer().setTargetFrameRate(number >= max ? -1 : number);
            }
        }, game.assetManager.localize("button.target_fps")){
            @Override
            protected String getText(){
                return this.number >= this.max ? this.text+": "+game.assetManager.localize("info.unlimited") : super.getText();
            }
        });

        this.chatNameField = new ComponentInputField(this, this.guiLeft, this.guiTop+90, 150, 16, true, true, true, false);
        this.chatNameField.setText(game.settings.chatName);
        this.components.add(this.chatNameField);

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, game.assetManager.localize("button.back")));
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        manager.getFont().drawCenteredString(this.guiLeft+75, this.guiTop+82, manager.localize("button.chat_name")+":", 0.35F, false);
    }

    @Override
    public boolean onButtonActivated(Game game, int button){
        if(button == -1){
            game.guiManager.openGui(this.parent);
            return true;
        }
        else if(button == 0){
            game.guiManager.openGui(new GuiKeybinds(this));
        }
        else if(button == 1){
            game.settings.hardwareCursor = !game.settings.hardwareCursor;
            game.assetManager.reloadCursor(game);
        }
        return false;
    }

    @Override
    public void onClosed(Game game){
        String name = this.chatNameField.getText();
        if(name != null && !name.isEmpty()){
            game.settings.chatName = name;
        }

        game.dataManager.saveSettings(game.settings);
    }
}
