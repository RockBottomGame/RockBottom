package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.*;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import org.lwjgl.opengl.Display;

public class GuiGraphics extends Gui{

    public GuiGraphics(Gui parent){
        super(304, 150, parent);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);
        Settings settings = game.getSettings();
        IAssetManager assetManager = game.getAssetManager();

        this.components.add(new ComponentToggleButton(this, 0, 0, 150, 16, settings.hardwareCursor, () -> {
            settings.hardwareCursor = !settings.hardwareCursor;
            game.getAssetManager().setCursor(game, game.getGuiManager().getCursor());
            return true;
        }, "button.hardware_cursor", assetManager.localize(RockBottomAPI.createInternalRes("info.hardware_cursor"))));
        this.components.add(new ComponentToggleButton(this, 0, 20, 150, 16, settings.cursorInfos, () -> {
            settings.cursorInfos = !settings.cursorInfos;
            return true;
        }, "button.cursor_infos", assetManager.localize(RockBottomAPI.createInternalRes("info.cursor_infos"))));
        this.components.add(new ComponentSlider(this, 0, 40, 150, 16, (int)(settings.textSpeed*10F), 1, 100, ((integer, aBoolean) -> settings.textSpeed = (float)integer/10F), assetManager.localize(RockBottomAPI.createInternalRes("button.text_speed"))));

        this.components.add(new ComponentSlider(this, 154, 0, 150, 16, (int)(settings.renderScale*100F), 50, 150, (integer, aBoolean) -> {
            settings.renderScale = (float)integer/100F;
            game.getGraphics().calcScales();
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.render_scale"))));
        this.components.add(new ComponentSlider(this, 154, 20, 150, 16, (int)(settings.guiScale*100F), 50, 100, (integer, aBoolean) -> {
            if(aBoolean){
                settings.guiScale = (float)integer/100F;
                game.getDataManager().savePropSettings(settings);
                game.getGraphics().calcScales();
                game.getGuiManager().updateDimensions();
            }
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.gui_scale"))));
        this.components.add(new ComponentSlider(this, 154, 40, 150, 16, settings.targetFps, 30, 256, ((integer, aBoolean) -> settings.targetFps = integer), assetManager.localize(RockBottomAPI.createInternalRes("button.target_fps"))){
            @Override
            protected String getText(){
                return this.number >= this.max ? this.text+": "+assetManager.localize(RockBottomAPI.createInternalRes("info.unlimited")) : super.getText();
            }
        });
        this.components.add(new ComponentToggleButton(this, 154, 60, 150, 16, !settings.fullscreen, () -> {
            settings.fullscreen = !settings.fullscreen;
            game.getDataManager().savePropSettings(settings);
            game.setFullscreen(settings.fullscreen);
            return true;
        }, "button.fullscreen"));
        this.components.add(new ComponentToggleButton(this, 154, 80, 150, 16, !settings.vsync, () -> {
            settings.vsync = !settings.vsync;
            Display.setVSyncEnabled(settings.vsync);
            return true;
        }, "button.vsync"));
        this.components.add(new ComponentToggleButton(this, 154, 100, 150, 16, !settings.smoothLighting, () -> {
            settings.smoothLighting = !settings.smoothLighting;
            return true;
        }, "button.smooth_lighting"));

        this.components.add(new ComponentColorPicker(this, 55, 70, 40, 40, settings.guiColor, (color, letGo) -> settings.guiColor = color, false));
        this.components.add(new ComponentFancyButton(this, 99, 94, 16, 16, () -> {
            this.components.add(new ComponentConfirmationPopup(this, 99+8, 94+8, aBoolean -> {
                if(aBoolean){
                    settings.guiColor = Settings.DEFAULT_GUI_COLOR;
                }
            }));
            this.sortComponents();
            return true;
        }, RockBottomAPI.createInternalRes("gui.reset"), assetManager.localize(RockBottomAPI.createInternalRes("info.reset"))));

        this.components.add(new ComponentButton(this, this.width/2-40, this.height-16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, assetManager.localize(RockBottomAPI.createInternalRes("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g){
        manager.getFont().drawCenteredString(this.x+75, this.y+62, manager.localize(RockBottomAPI.createInternalRes("info.gui_color")), 0.35F, false);

        super.render(game, manager, g);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("graphics");
    }
}
