package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class ComponentCheckBox extends GuiComponent{

    private boolean state;

    public ComponentCheckBox(Gui gui, int x, int y, int width, int height, boolean defaultState){
        super(gui, x, y, width, height);
        this.state = defaultState;
    }

    public boolean isActivated(){
        return this.state;
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(Settings.KEY_GUI_ACTION_1.isKey(button) && this.isMouseOver(game)){
            this.state = !this.state;
            game.getAssetManager().getSound(RockBottomAPI.createInternalRes("menu.click")).play();
            return true;
        }
        return false;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, int x, int y){
        super.render(game, manager, g, x, y);

        g.fillRect(x, y, this.width, this.height, this.isMouseOverPrioritized(game) ? getElementColor() : getUnselectedElementColor());
        g.drawRect(x, y, this.width, this.height, getElementOutlineColor());

        if(this.state){
            manager.getFont().drawCenteredString(x+this.width/2, y+this.height/2+1, "X", 0.5F, true);
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("checkbox");
    }
}
