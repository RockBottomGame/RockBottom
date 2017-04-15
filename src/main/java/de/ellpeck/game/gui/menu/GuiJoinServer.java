package de.ellpeck.game.gui.menu;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.gui.component.ComponentButton;
import de.ellpeck.game.gui.component.ComponentInputField;

public class GuiJoinServer extends Gui{

    private ComponentInputField inputField;

    public GuiJoinServer(Gui parent){
        super(100, 100, parent);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        this.inputField = new ComponentInputField(this, this.guiLeft+this.sizeX/2-80, 50, 160, 16);
        this.components.add(this.inputField);

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, (int)game.getHeightInGui()-30, 80, 16, game.assetManager.localize("button.back")));
    }

    @Override
    public boolean onButtonActivated(Game game, int button){
        if(button == -1){
            game.guiManager.openGui(this.parent);
            return true;
        }
        return false;
    }
}
