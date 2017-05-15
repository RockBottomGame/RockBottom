package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.data.settings.Settings;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.gui.component.ComponentButton;
import de.ellpeck.rockbottom.gui.component.ComponentColorPicker;
import de.ellpeck.rockbottom.gui.component.ComponentSlider;
import de.ellpeck.rockbottom.gui.component.ComponentToggleButton;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class GuiGraphics extends Gui{

    public GuiGraphics(Gui parent){
        super(304, 135, parent);
    }

    @Override
    public void initGui(RockBottom game){
        super.initGui(game);

        this.components.add(new ComponentToggleButton(this, 0, this.guiLeft, this.guiTop, 150, 16, game.settings.hardwareCursor, "button.hardware_cursor", game.assetManager.localize("info.hardware_cursor")));
        this.components.add(new ComponentToggleButton(this, 5, this.guiLeft, this.guiTop+20, 150, 16, game.settings.cursorInfos, "button.cursor_infos", game.assetManager.localize("info.cursor_infos")));

        this.components.add(new ComponentSlider(this, 3, this.guiLeft+154, this.guiTop, 150, 16, game.settings.renderScale, 1, 128, new ComponentSlider.ICallback(){
            @Override
            public void onLetGo(float mouseX, float mouseY, int min, int max, int number){
                game.settings.renderScale = number;
            }
        }, game.assetManager.localize("button.render_scale")));
        this.components.add(new ComponentSlider(this, 2, this.guiLeft+154, this.guiTop+20, 150, 16, game.settings.guiScale, 1, 8, new ComponentSlider.ICallback(){
            @Override
            public void onLetGo(float mouseX, float mouseY, int min, int max, int number){
                game.settings.guiScale = number;
                game.dataManager.savePropSettings(game.settings);
                game.guiManager.shouldReInit = true;
            }
        }, game.assetManager.localize("button.gui_scale")));
        this.components.add(new ComponentSlider(this, 4, this.guiLeft+154, this.guiTop+40, 150, 16, game.settings.targetFps, 30, 256, new ComponentSlider.ICallback(){
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

        this.components.add(new ComponentColorPicker(this, this.guiLeft+55, this.guiTop+70, 40, 40, game.settings.guiColor, new ComponentColorPicker.ICallback(){
            @Override
            public void onLetGo(float mouseX, float mouseY, Color color){
                game.settings.guiColor = color;
                game.dataManager.savePropSettings(game.settings);
                game.guiManager.shouldReInit = true;
            }
        }));
        this.components.add(new ComponentButton(this, 6, this.guiLeft+99, this.guiTop+94, 16, 16, "!", game.assetManager.localize("info.reset")));

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, game.assetManager.localize("button.back")));
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        manager.getFont().drawCenteredString(this.guiLeft+75, this.guiTop+62, manager.localize("info.gui_color"), 0.35F, false);
    }

    @Override
    public boolean onButtonActivated(RockBottom game, int button){
        if(button == -1){
            game.guiManager.openGui(this.parent);
            return true;
        }
        else if(button == 0){
            game.settings.hardwareCursor = !game.settings.hardwareCursor;
            game.assetManager.reloadCursor(game);
            return true;
        }
        else if(button == 5){
            game.settings.cursorInfos = !game.settings.cursorInfos;
            return true;
        }
        else if(button == 6){
            game.settings.guiColor = new Color(Settings.DEFAULT_GUI_R, Settings.DEFAULT_GUI_G, Settings.DEFAULT_GUI_B);
            game.dataManager.savePropSettings(game.settings);
            game.guiManager.shouldReInit = true;
            return true;
        }
        return false;
    }
}
