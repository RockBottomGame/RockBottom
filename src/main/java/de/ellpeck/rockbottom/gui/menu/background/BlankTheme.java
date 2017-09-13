package de.ellpeck.rockbottom.gui.menu.background;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.gui.IMainMenuTheme;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import org.newdawn.slick.Color;

public class BlankTheme implements IMainMenuTheme{

    @Override
    public TileState getState(int x, int y){
        return GameContent.TILE_AIR.getDefState();
    }

    @Override
    public Color getBackgroundColor(){
        return Color.darkGray;
    }
}
