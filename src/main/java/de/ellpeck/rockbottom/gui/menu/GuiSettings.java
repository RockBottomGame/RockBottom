package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlider;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class GuiSettings extends Gui {

    public GuiSettings(Gui parent) {
        super(304, 150, parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        IAssetManager assetManager = game.getAssetManager();
        Settings settings = game.getSettings();
        IGuiManager guiManager = game.getGuiManager();

        this.components.add(new ComponentButton(this, this.width / 2 - 75, 0, 150, 16, () -> {
            guiManager.openGui(new GuiKeybinds(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.controls"))));
        this.components.add(new ComponentButton(this, this.width / 2 - 75, 20, 150, 16, () -> {
            guiManager.openGui(new GuiGraphics(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.graphics"))));
        this.components.add(new ComponentButton(this, this.width / 2 - 75, 40, 150, 16, () -> {
            guiManager.openGui(new GuiLanguage(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.language"))));
        this.components.add(new ComponentButton(this, this.width / 2 - 75, 60, 150, 16, () -> {
            guiManager.openGui(new GuiSound(this));
            return true;
        }, assetManager.localize(ResourceName.intern("button.sound"))));

        this.components.add(new ComponentSlider(this, this.width / 2 - 75, 85, 150, 16, settings.autosaveIntervalSeconds, 30, 1800, ((integer, aBoolean) -> settings.autosaveIntervalSeconds = integer), assetManager.localize(ResourceName.intern("button.autosave_interval")), assetManager.localize(ResourceName.intern("info.autosave_interval"))));

        this.components.add(new ComponentButton(this, this.width / 2 - 40, this.height - 16, 80, 16, () -> {
            guiManager.openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("settings");
    }

    @Override
    public void onClosed(IGameInstance game) {
        super.onClosed(game);
        game.getSettings().save();
    }
}
