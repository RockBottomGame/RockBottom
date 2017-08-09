package de.ellpeck.rockbottom.gui.menu.background;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.gui.IMainMenuTheme;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.render.WorldRenderer;
import org.newdawn.slick.Color;

public class OreWallTheme implements IMainMenuTheme{

    @Override
    public TileState getState(int x, int y){
        Tile tile;

        float f = Util.RANDOM.nextFloat();
        if(f >= 0.9F){
            tile = GameContent.TILE_COPPER_ORE;
        }
        else if(f >= 0.75F){
            tile = GameContent.TILE_COAL_ORE;
        }
        else{
            tile = GameContent.TILE_STONE;
        }

        return tile.getDefState();
    }

    @Override
    public Color getBackgroundColor(){
        return WorldRenderer.SKY_COLORS[WorldRenderer.SKY_COLORS.length-1];
    }
}
