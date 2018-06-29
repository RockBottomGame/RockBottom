package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.*;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.GuiManager;

public class GuiGraphics extends Gui {

    private static final String RAINBOW = "do the disco dance";
    private int rainbowIndex;

    public GuiGraphics(Gui parent) {
        super(304, 150, parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        Settings settings = game.getSettings();
        IAssetManager assetManager = game.getAssetManager();

        this.components.add(new ComponentToggleButton(this, 0, 0, 150, 16, settings.hardwareCursor, () -> {
            settings.hardwareCursor = !settings.hardwareCursor;
            game.getAssetManager().setCursor(game.getGuiManager().getCursor());
            return true;
        }, "button.hardware_cursor", assetManager.localize(ResourceName.intern("info.hardware_cursor"))));
        this.components.add(new ComponentToggleButton(this, 0, 20, 150, 16, settings.cursorInfos, () -> {
            settings.cursorInfos = !settings.cursorInfos;
            return true;
        }, "button.cursor_infos", assetManager.localize(ResourceName.intern("info.cursor_infos"))));
        this.components.add(new ComponentSlider(this, 0, 40, 150, 16, (int) (settings.textSpeed * 10F), 1, 100, ((integer, aBoolean) -> settings.textSpeed = (float) integer / 10F), assetManager.localize(ResourceName.intern("button.text_speed"))));

        this.components.add(new ComponentSlider(this, 154, 0, 150, 16, (int) (settings.renderScale * 100F), 50, 150, (integer, aBoolean) -> {
            settings.renderScale = (float) integer / 100F;
            game.getRenderer().calcScales();
        }, assetManager.localize(ResourceName.intern("button.render_scale"))));
        this.components.add(new ComponentSlider(this, 154, 20, 150, 16, (int) (settings.guiScale * 100F), 50, 100, (integer, aBoolean) -> {
            if (aBoolean) {
                settings.guiScale = (float) integer / 100F;
                settings.save();
                game.getRenderer().calcScales();
                game.getGuiManager().updateDimensions();
            }
        }, assetManager.localize(ResourceName.intern("button.gui_scale"))));
        this.components.add(new ComponentToggleButton(this, 154, 40, 150, 16, !settings.fullscreen, () -> {
            settings.fullscreen = !settings.fullscreen;
            settings.save();
            game.setFullscreen(settings.fullscreen);
            return true;
        }, "button.fullscreen"));
        this.components.add(new ComponentToggleButton(this, 154, 60, 150, 16, !settings.smoothLighting, () -> {
            settings.smoothLighting = !settings.smoothLighting;
            return true;
        }, "button.smooth_lighting"));

        this.components.add(new ComponentColorPicker(this, 55, 70, 40, 40, settings.guiColor, (color, letGo) -> settings.guiColor = color, false));
        this.components.add(new ComponentFancyButton(this, 99, 94, 16, 16, () -> {
            this.components.add(new ComponentConfirmationPopup(this, 99 + 8, 94 + 8, aBoolean -> {
                if (aBoolean) {
                    settings.guiColor = Settings.DEFAULT_GUI_COLOR;
                    GuiManager.rainbowMode = false;
                }
            }));
            this.sortComponents();
            return true;
        }, ResourceName.intern("gui.reset"), assetManager.localize(ResourceName.intern("info.reset"))));

        this.components.add(new ComponentButton(this, this.width / 2 - 40, this.height - 16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public boolean onCharInput(IGameInstance game, int codePoint, char[] characters) {
        boolean did = false;
        for (char c : characters) {
            if (this.rainbowIndex < RAINBOW.length()) {
                if (RAINBOW.charAt(this.rainbowIndex) == c) {
                    this.rainbowIndex++;

                    if (this.rainbowIndex >= RAINBOW.length()) {
                        GuiManager.rainbowMode = true;
                        this.rainbowIndex = 0;
                    }

                    did = true;
                } else {
                    this.rainbowIndex = 0;
                }
            }
        }
        return did;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        manager.getFont().drawCenteredString(this.x + 75, this.y + 62, manager.localize(ResourceName.intern("info.gui_color")), 0.35F, false);

        super.render(game, manager, g);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("graphics");
    }
}
