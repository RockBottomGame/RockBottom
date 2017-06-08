package de.ellpeck.rockbottom.game.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.gui.Gui;
import de.ellpeck.rockbottom.game.gui.component.ComponentToggleButton;
import de.ellpeck.rockbottom.game.gui.component.ComponentButton;
import de.ellpeck.rockbottom.game.gui.component.ComponentSelectWorldButton;

public class GuiSelectWorld extends Gui{

    public boolean deleteMode;

    public GuiSelectWorld(Gui parent){
        super(100, 100, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        int xStart = (int)game.getWidthInGui()/2-264/2;

        int x = 0;
        int y = 0;
        for(int i = 0; i < 6; i++){
            this.components.add(new ComponentSelectWorldButton(this, i, x+xStart, y+50, 130, 16));

            y += 20;
            if(i == 2){
                x += 134;
                y = 0;
            }
        }

        int bottomY = (int)game.getHeightInGui();
        this.components.add(new ComponentToggleButton(this, -2, this.guiLeft+this.sizeX/2-60, bottomY-50, 120, 16, this.deleteMode, "button.delete", "Activates Delete mode. Click a world's button to delete it"));
        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, bottomY-30, 80, 16, game.getAssetManager().localize("button.back")));
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        if(button == -1){
            game.getGuiManager().openGui(this.parent);
            return true;
        }
        else if(button == -2){
            this.deleteMode = !this.deleteMode;
            return true;
        }
        return false;
    }
}
