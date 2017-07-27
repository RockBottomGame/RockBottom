package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentScrollBar;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.gui.component.ComponentSelectWorldButton;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiSelectWorld extends Gui{

    private ComponentScrollBar scrollBar;
    private static final int BUTTON_AMOUNT = 5;
    private final ComponentSelectWorldButton[] buttons = new ComponentSelectWorldButton[BUTTON_AMOUNT];
    private final ComponentButton[] deleteButtons = new ComponentButton[BUTTON_AMOUNT];

    public GuiSelectWorld(Gui parent){
        super(200, 100, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        for(int i = 0; i < BUTTON_AMOUNT; i++){
            ComponentSelectWorldButton button = new ComponentSelectWorldButton(this, 1+i, this.guiLeft, this.guiTop+(i*18), 182, 16);
            this.buttons[i] = button;
            this.components.add(button);

            this.deleteButtons[i] = new ComponentButton(this, this.buttons.length+1+i, this.guiLeft+184, this.guiTop+(i*18), 16, 16, "X", "Delete World"){
                @Override
                public boolean onPressed(IGameInstance game){
                    try{
                        Util.deleteFolder(button.worldFile);
                        Log.info("Successfully deleted world "+button.worldFile);
                    }
                    catch(Exception e){
                        Log.error("Couldn't delete world "+button.worldFile, e);
                    }

                    GuiSelectWorld.this.populateButtons(game);
                    return true;
                }
            };
            this.components.add(this.deleteButtons[i]);
        }

        BoundBox box = new BoundBox(0, 0, 200, this.sizeY-12).add(this.guiLeft, this.guiTop);
        this.scrollBar = new ComponentScrollBar(this, 0, this.guiLeft-8, this.guiTop, 6, this.sizeY-12, 0, 0, 0, box, (min, max, number) -> this.populateButtons(game));
        this.components.add(this.scrollBar);

        int bottomY = (int)game.getHeightInGui();
        this.components.add(new ComponentButton(this, -2, this.guiLeft+this.sizeX/2-82, bottomY-30, 80, 16, "Create World"));

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2+2, bottomY-30, 80, 16, game.getAssetManager().localize(AbstractGame.internalRes("button.back"))));

        this.populateButtons(game);
    }

    private void populateButtons(IGameInstance game){
        File worldFolder = game.getDataManager().getWorldsDir();
        if(worldFolder.exists()){
            File[] worlds = worldFolder.listFiles();
            if(worlds != null && worlds.length > 0){
                List<File> validWorlds = new ArrayList<>();

                for(File world : worlds){
                    if(WorldInfo.exists(world)){
                        validWorlds.add(world);
                    }
                }

                int offset = this.scrollBar.getNumber();
                for(int i = 0; i < BUTTON_AMOUNT; i++){
                    ComponentSelectWorldButton button = this.buttons[i];
                    ComponentButton deleteButton = this.deleteButtons[i];

                    if(validWorlds.size() > offset+i){
                        button.setWorld(validWorlds.get(offset+i));

                        button.isVisible = true;
                        deleteButton.isVisible = true;
                    }
                    else{
                        button.isVisible = false;
                        deleteButton.isVisible = false;
                    }
                }

                boolean locked = validWorlds.size() < this.buttons.length;
                this.scrollBar.setLocked(locked);
                if(!locked){
                    this.scrollBar.setMax(validWorlds.size()-5);
                }
            }
        }
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        if(button == -1){
            game.getGuiManager().openGui(this.parent);
            return true;
        }
        else if(button == -2){
            game.getGuiManager().openGui(new GuiCreateWorld(this));
        }
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("select_world");
    }
}
