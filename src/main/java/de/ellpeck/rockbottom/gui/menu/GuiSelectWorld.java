package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentConfirmationPopup;
import de.ellpeck.rockbottom.api.gui.component.ComponentScrollMenu;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyButton;
import de.ellpeck.rockbottom.gui.component.ComponentSelectWorldButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

public class GuiSelectWorld extends Gui{

    public GuiSelectWorld(Gui parent){
        super(200, 160, parent);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        BoundBox box = new BoundBox(0, 0, 200, 128).add(this.getX(), this.getY());
        ComponentScrollMenu menu = new ComponentScrollMenu(this, -8, 0, 128, 2, 5, box);
        this.components.add(menu);

        int bottomY = this.height;
        this.components.add(new ComponentButton(this, this.width/2-82, bottomY-30, 80, 16, () -> {
            game.getGuiManager().openGui(new GuiCreateWorld(this));
            return true;
        }, "Create World"));

        this.components.add(new ComponentButton(this, this.width/2, bottomY-30, 62, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.back"))));

        this.components.add(new ComponentFancyButton(this, this.width/2+64, bottomY-30, 16, 16, ()-> Util.createAndOpen(game.getDataManager().getWorldsDir()), RockBottomAPI.createInternalRes("gui.worlds_folder"), game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.worlds_folder"))));

        File worldFolder = game.getDataManager().getWorldsDir();
        File[] worlds = worldFolder.listFiles();
        List<File> validWorlds = new ArrayList<>();

        if(worlds != null && worlds.length > 0){
            for(File world : worlds){
                if(WorldInfo.exists(world)){
                    validWorlds.add(world);
                }
            }
        }

        validWorlds.sort(Comparator.comparingLong(WorldInfo:: lastModified).reversed());

        for(File file : validWorlds){
            ComponentSelectWorldButton button = new ComponentSelectWorldButton(this, 0, 0, file);
            menu.add(button);

            menu.add(new ComponentFancyButton(this, 0, 0, 12, 12, null, RockBottomAPI.createInternalRes("gui.delete"), "Delete World"){
                @Override
                public boolean onPressed(IGameInstance game){
                    this.gui.getComponents().add(0, new ComponentConfirmationPopup(this.gui, this.x+this.width/2, this.y+this.height/2, aBoolean -> {
                        if(aBoolean){
                            try{
                                Util.deleteFolder(button.worldFile);
                                RockBottomAPI.logger().info("Successfully deleted world "+button.worldFile);
                            }
                            catch(Exception e){
                                RockBottomAPI.logger().log(Level.WARNING, "Couldn't delete world "+button.worldFile, e);
                            }

                            menu.remove(this);
                            menu.remove(button);
                            menu.organize();
                        }
                    }));
                    return true;
                }
            });
        }

        menu.organize();
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("select_world");
    }
}
