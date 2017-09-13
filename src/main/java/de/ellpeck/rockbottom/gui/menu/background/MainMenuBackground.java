package de.ellpeck.rockbottom.gui.menu.background;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.tex.Texture;
import de.ellpeck.rockbottom.api.gui.IMainMenuTheme;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.init.RockBottom;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.List;

public class MainMenuBackground{

    private static final IResourceName RES_LOGO = AbstractGame.internalRes("logo");

    static{
        RockBottomAPI.MAIN_MENU_THEMES.add(new BlankTheme());
    }

    private final IMainMenuTheme theme;
    private final TileState[][] tiles = new TileState[IMainMenuTheme.TILE_AMOUNT][IMainMenuTheme.TILE_AMOUNT];

    private int currentY;
    private int layerCounter;

    public MainMenuBackground(){
        List<IMainMenuTheme> themes = RockBottomAPI.MAIN_MENU_THEMES;
        this.theme = themes.get(Util.RANDOM.nextInt(themes.size()));
    }

    public void update(RockBottom game){
        if(this.currentY < IMainMenuTheme.TILE_AMOUNT){
            if(game.getTotalTicks()%2 == 0 && Util.RANDOM.nextFloat() >= 0.75F){
                int placeX;
                do{
                    placeX = Util.RANDOM.nextInt(IMainMenuTheme.TILE_AMOUNT);
                }
                while(this.tiles[placeX][this.currentY] != null);

                this.tiles[placeX][this.currentY] = this.theme.getState(placeX, this.currentY);
                this.layerCounter++;

                if(this.layerCounter >= IMainMenuTheme.TILE_AMOUNT){
                    this.currentY++;
                    this.layerCounter = 0;
                }
            }
        }
    }

    public void render(RockBottom game, IAssetManager manager, Graphics g){
        g.setBackground(this.theme.getBackgroundColor());

        float height = game.getHeightInGui();
        float tileSize = Math.max(game.getWidthInGui(), height)/(float)IMainMenuTheme.TILE_AMOUNT;

        for(int x = 0; x < IMainMenuTheme.TILE_AMOUNT; x++){
            for(int y = 0; y < IMainMenuTheme.TILE_AMOUNT; y++){
                TileState state = this.tiles[x][y];
                if(state != null){
                    Tile tile = state.getTile();
                    ITileRenderer renderer = tile.getRenderer();
                    if(renderer != null){
                        renderer.renderInMainMenuBackground(game, manager, g, tile, state, x*tileSize, height-(y+1)*tileSize, tileSize);
                    }
                }
            }
        }

        Texture logo = manager.getTexture(RES_LOGO);
        logo.draw((int)game.getWidthInGui()/2-logo.getWidth()/2, (int)game.getHeightInGui()/3-logo.getHeight()/2);
    }
}
