package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.*;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.gui.component.ComponentSelectWorldButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

public class GuiSelectWorld extends Gui {

    public GuiSelectWorld(Gui parent) {
        super(parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        IAssetManager manager = game.getAssetManager();

        int menuX = this.width / 2 - (186 + 14 + 8) / 2;
        BoundingBox box = new BoundingBox(0, 0, 186 + 14 + 8, 138).add(this.getX() + menuX, this.getY() + 5);
        ComponentMenu menu = new ComponentMenu(this, menuX, 5, 138, 1, 5, box);
        this.components.add(menu);

        this.components.add(new ComponentButton(this, this.width / 2 - 82, this.height - 30, 80, 16, () -> {
            game.getGuiManager().openGui(new GuiCreateWorld(this));
            return true;
        }, manager.localize(ResourceName.intern("button.create_world"))));

        this.components.add(new ComponentButton(this, this.width / 2, this.height - 30, 62, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.back"))));

        this.components.add(new ComponentFancyButton(this, this.width / 2 + 64, this.height - 30, 16, 16, () -> Util.createAndOpen(game.getDataManager().getWorldsDir()), ResourceName.intern("gui.worlds_folder"), game.getAssetManager().localize(ResourceName.intern("button.worlds_folder"))));

        File worldFolder = game.getDataManager().getWorldsDir();
        File[] worlds = worldFolder.listFiles();
        List<File> validWorlds = new ArrayList<>();

        if (worlds != null && worlds.length > 0) {
            for (File world : worlds) {
                if (WorldInfo.exists(world)) {
                    validWorlds.add(world);
                }
            }
        }

        if (validWorlds.isEmpty()) {
            game.getGuiManager().openGui(new GuiCreateWorld(this.parent));
            return;
        }

        validWorlds.sort(Comparator.comparingLong(WorldInfo::lastModified).reversed());

        for (File file : validWorlds) {
            MenuComponent component = new MenuComponent(186 + 14, 26);

            ComponentSelectWorldButton button = new ComponentSelectWorldButton(this, 0, 0, file);
            component.add(0, 0, button);

            component.add(186 + 2, 0, new ComponentFancyButton(this, 0, 0, 12, 12, null, ResourceName.intern("gui.delete"), manager.localize(ResourceName.intern("button.delete_world"))) {
                @Override
                public boolean onPressed(IGameInstance game) {
                    this.gui.getComponents().add(new ComponentConfirmationPopup(this.gui, this.x + this.width / 2, this.y + this.height / 2, aBoolean -> {
                        if (aBoolean) {
                            try {
                                Util.deleteFolder(button.worldFile);
                                RockBottomAPI.logger().info("Successfully deleted world " + button.worldFile);
                            } catch (Exception e) {
                                RockBottomAPI.logger().log(Level.WARNING, "Couldn't delete world " + button.worldFile, e);
                            }

                            menu.remove(component);
                            menu.organize();
                        }
                    }));
                    this.gui.sortComponents();
                    return true;
                }
            });

            component.add(186 + 2, 14, new ComponentFancyButton(this, 0, 0, 12, 12, () -> {
                game.getGuiManager().openGui(new GuiRenameWorld(this, file));
                return true;
            }, ResourceName.intern("gui.rename"), manager.localize(ResourceName.intern("button.rename_world"))));

            menu.add(component);
        }

        menu.organize();
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("select_world");
    }
}
