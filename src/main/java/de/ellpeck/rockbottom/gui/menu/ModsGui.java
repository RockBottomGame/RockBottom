package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.data.settings.ModSettings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.*;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.logging.Level;

public class ModsGui extends Gui {

    private IMod selectedMod;
    private ButtonComponent disableButton;
    private ButtonComponent modGuiButton;

    public ModsGui(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        int height = 126;
        int y = this.height / 2 - height / 2 - 10;
        MenuComponent menu = new MenuComponent(this, 10, y, height, 1, 8, new BoundingBox(0, 0, 100, height).add(18, y));
        this.components.add(menu);

        for (IMod mod : RockBottomAPI.getModLoader().getAllTheMods()) {
            if (this.selectedMod == null) {
                this.selectedMod = mod;
            }

            menu.add(new MenuItemComponent(100, 16).add(0, 0, new ButtonComponent(this, 0, 0, 100, 16, () -> {
                this.selectedMod = mod;
                this.updateButtons();
                return true;
            }, mod.getDisplayName())));
        }

        menu.organize();

        this.modGuiButton = new ButtonComponent(this, 118 + (this.width - 118) / 2 - 81, this.height - 55, 80, 16, () -> {
            Class<? extends Gui> guiClass = this.selectedMod.getModGuiClass();
            if (guiClass != null) {
                try {
                    Gui gui = guiClass.getConstructor(Gui.class).newInstance(this);
                    game.getGuiManager().openGui(gui);
                    return true;
                } catch (Exception e) {
                    RockBottomAPI.logger().log(Level.WARNING, "Failed initializing mod gui for mod " + this.selectedMod.getDisplayName(), e);
                }
            }
            return false;
        }, "Mod Gui");
        this.components.add(this.modGuiButton);

        this.disableButton = new ButtonComponent(this, 118 + (this.width - 118) / 2 + 1, this.height - 55, 80, 16, () -> {
            ModSettings settings = RockBottomAPI.getModLoader().getModSettings();
            settings.setDisabled(this.selectedMod.getId(), !settings.isDisabled(this.selectedMod.getId()));
            settings.save();

            this.updateButtons();
            return true;
        }, "");
        this.components.add(this.disableButton);

        this.updateButtons();

        this.components.add(new ImageButtonComponent(this, this.width / 2 + 83, this.height - 30, 16, 16, () -> Util.createAndOpen(game.getDataManager().getModsDir()), ResourceName.intern("gui.mods_folder"), game.getAssetManager().localize(ResourceName.intern("button.mods_folder"))));

        this.components.add(new ButtonComponent(this, this.width / 2 + 1, this.height - 30, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.back"))));

        this.components.add(new ButtonComponent(this, this.width / 2 - 81, this.height - 30, 80, 16, () -> {
            this.components.add(new ConfirmationPopupComponent(this, this.width / 2 - 41, this.height - 22, aBoolean -> {
                if (aBoolean) {
                    game.restart();
                }
            }));
            this.sortComponents();
            return true;
        }, "Restart Game"));

    }

    private void updateButtons() {
        this.modGuiButton.setActive(this.selectedMod.getModGuiClass() != null);

        this.disableButton.setActive(this.selectedMod.isDisableable());
        this.disableButton.setText(RockBottomAPI.getModLoader().getModSettings().isDisabled(this.selectedMod.getId()) ? "Enable" : "Disable");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        IFont font = manager.getFont();
        font.drawCenteredString(118 + (this.width - 118) / 2, 15, this.selectedMod.getDisplayName(), 0.75F, false);
        font.drawCenteredString(118 + (this.width - 118) / 2, 32, "Version " + this.selectedMod.getVersion(), 0.35F, false);

        StringBuilder strg = new StringBuilder("Authors: ");
        String[] authors = this.selectedMod.getAuthors();
        for (int i = 0; i < authors.length; i++) {
            strg.append(authors[i]);
            if (i < authors.length - 1) {
                strg.append(", ");
            }
        }
        font.drawSplitString(125, 45, strg.toString(), 0.35F, this.width - 135);

        font.drawSplitString(125, 65, this.selectedMod.getDescription(), 0.35F, this.width - 135);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("mods");
    }
}
