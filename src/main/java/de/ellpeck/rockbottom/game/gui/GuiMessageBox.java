package de.ellpeck.rockbottom.game.gui;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.gui.component.ComponentMessageBox;

public class GuiMessageBox extends Gui{

    private final float textScale;
    private final String[] locKeys;

    public GuiMessageBox(int sizeX, int sizeY, float textScale, String... locKeys){
        super(sizeX, sizeY);
        this.textScale = textScale;
        this.locKeys = locKeys;
    }

    @Override
    public void initGui(RockBottom game){
        super.initGui(game);

        this.components.add(new ComponentMessageBox(this, 0, this.guiLeft, this.guiTop, this.sizeX, this.sizeY, this.textScale, this.locKeys));
    }

    @Override
    public boolean onButtonActivated(RockBottom game, int button){
        if(button == 0){
            game.guiManager.closeGui();
            return true;
        }
        else{
            return false;
        }
    }
}
