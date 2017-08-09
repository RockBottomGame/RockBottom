package de.ellpeck.rockbottom.gui.menu.background;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.gui.IMainMenuTheme;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import org.newdawn.slick.Color;

public class CheckerboardTheme implements IMainMenuTheme{

    @Override
    public TileState getState(int x, int y){
        return ((x%2 == 0) != (y%2 == 0) ? GameContent.TILE_HARDENED_STONE : GameContent.TILE_STONE).getDefState();
    }

    @Override
    public Color getBackgroundColor(){
        return Color.darkGray;
    }
}
