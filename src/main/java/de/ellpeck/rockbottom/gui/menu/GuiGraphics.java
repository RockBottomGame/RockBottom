package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentConfirmationPopup;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlider;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentColorPicker;
import de.ellpeck.rockbottom.gui.component.ComponentFancyButton;
import de.ellpeck.rockbottom.gui.component.ComponentToggleButton;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class GuiGraphics extends Gui{

    public GuiGraphics(Gui parent){
        super(304, 150, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);
        Settings settings = game.getSettings();
        IAssetManager assetManager = game.getAssetManager();

        this.components.add(new ComponentToggleButton(this, this.guiLeft, this.guiTop, 150, 16, settings.hardwareCursor, () -> {
            settings.hardwareCursor = !settings.hardwareCursor;
            game.getAssetManager().reloadCursor(game);
            return true;
        }, "button.hardware_cursor", assetManager.localize(AbstractGame.internalRes("info.hardware_cursor"))));
        this.components.add(new ComponentToggleButton(this, this.guiLeft, this.guiTop+20, 150, 16, settings.cursorInfos, () -> {
            settings.cursorInfos = !settings.cursorInfos;
            return true;
        }, "button.cursor_infos", assetManager.localize(AbstractGame.internalRes("info.cursor_infos"))));
        this.components.add(new ComponentSlider(this, this.guiLeft, this.guiTop+40, 150, 16, (int)(settings.textSpeed*10F), 1, 100, ((integer, aBoolean) -> settings.textSpeed = (float)integer/10F), assetManager.localize(AbstractGame.internalRes("button.text_speed"))));

        this.components.add(new ComponentSlider(this, this.guiLeft+154, this.guiTop, 150, 16, (int)(settings.renderScale*100F), 50, 150, (integer, aBoolean) -> {
            settings.renderScale = (float)integer/100F;
            game.calcScales();
        }, assetManager.localize(AbstractGame.internalRes("button.render_scale"))));
        this.components.add(new ComponentSlider(this, this.guiLeft+154, this.guiTop+20, 150, 16, (int)(settings.guiScale*100F), 50, 100, ((integer, aBoolean) -> {
            if(aBoolean){
                settings.guiScale = (float)integer/100F;
                game.getDataManager().savePropSettings(settings);
                game.getGuiManager().setReInit();
                game.calcScales();
            }
        }), assetManager.localize(AbstractGame.internalRes("button.gui_scale"))));
        this.components.add(new ComponentSlider(this, this.guiLeft+154, this.guiTop+40, 150, 16, settings.targetFps, 30, 256, ((integer, aBoolean) -> settings.targetFps = integer), assetManager.localize(AbstractGame.internalRes("button.target_fps"))){
            @Override
            protected String getText(){
                return this.number >= this.max ? this.text+": "+assetManager.localize(AbstractGame.internalRes("info.unlimited")) : super.getText();
            }
        });
        this.components.add(new ComponentToggleButton(this, this.guiLeft+154, this.guiTop+60, 150, 16, !settings.fullscreen, () -> {
            settings.fullscreen = !settings.fullscreen;
            game.getDataManager().savePropSettings(settings);
            game.setFullscreen(settings.fullscreen);
            return true;
        }, "button.fullscreen"));
        this.components.add(new ComponentToggleButton(this, this.guiLeft+154, this.guiTop+80, 150, 16, !settings.vsync, () -> {
            settings.vsync = !settings.vsync;
            Display.setVSyncEnabled(settings.vsync);
            return true;
        }, "button.vsync"));
        this.components.add(new ComponentToggleButton(this, this.guiLeft+154, this.guiTop+100, 150, 16, !settings.smoothLighting, () -> {
            settings.smoothLighting = !settings.smoothLighting;
            return true;
        }, "button.smooth_lighting"));

        this.components.add(new ComponentColorPicker(this, this.guiLeft+55, this.guiTop+70, 40, 40, settings.guiColor, (color, letGo) -> {
            if(letGo){
                settings.guiColor = color;
                game.getDataManager().savePropSettings(settings);
                game.getGuiManager().setReInit();
            }
        }, false));
        this.components.add(new ComponentFancyButton(this, this.guiLeft+99, this.guiTop+94, 16, 16, () -> {
            this.components.add(0, new ComponentConfirmationPopup(this, this.guiLeft+99+8, this.guiTop+94+8, aBoolean -> {
                if(aBoolean){
                    settings.guiColor = new Color(Settings.DEFAULT_GUI_R, Settings.DEFAULT_GUI_G, Settings.DEFAULT_GUI_B);
                    game.getDataManager().savePropSettings(settings);
                    game.getGuiManager().setReInit();
                }
            }));
            return true;
        }, RockBottomAPI.createInternalRes("gui.reset"), assetManager.localize(AbstractGame.internalRes("info.reset"))));

        this.components.add(new ComponentButton(this, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, assetManager.localize(AbstractGame.internalRes("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        manager.getFont().drawCenteredString(this.guiLeft+75, this.guiTop+62, manager.localize(AbstractGame.internalRes("info.gui_color")), 0.35F, false);

        super.render(game, manager, g);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("graphics");
    }
}
